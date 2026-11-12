package com.store.sugarbayy.services.objectStore;

import com.store.sugarbayy.configurations.exceptions.ApiException;
import com.store.sugarbayy.configurations.Common;
import com.store.sugarbayy.dtos.common.ClassInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ImageStorageServiceImpl implements ImageStorageService {
    private final S3Client s3Client;
    private final String apiUrl;
    private final String bucketName = "sugarbayy";

    public ImageStorageServiceImpl(
            S3Client s3Client,
            @Value("${s3.api-url}") String apiUrl
    ) {
        this.s3Client = s3Client;
        this.apiUrl = apiUrl;
    }


    @Override
    public String uploadImage(MultipartFile file, Object parentEntity) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("Image cannot be empty", HttpStatus.BAD_REQUEST);
        }

        ClassInfo parentEntityInfo = getClassInfo(parentEntity);

        String fileExtension = switch (Objects.requireNonNull(file.getContentType())) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/heic" -> ".heic";
            case "image/heif" -> ".heif";
            case "image/avif" -> ".avif";
            case "image/tiff", "image/tif" -> ".tiff";
            default -> throw new ApiException(
                    "Unsupported Image Type: " + file.getContentType(),
                    HttpStatus.BAD_REQUEST);
        };

        String key = parentEntityInfo.name() +
                "/" + parentEntityInfo.id() +
                "/" + Common.generateRandomString(6) + fileExtension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        } catch (Exception e) {
            log.error("Failed to upload image to R2. Key: {}", key, e);
            throw new ApiException("Unable to save images", HttpStatus.BAD_REQUEST);
        }

        return key;
    }

    @Override
    public void deleteImages(int remainingAttempts, String... keys) {
        if (remainingAttempts <= 0 || keys == null || keys.length == 0) {
            return;
        }
        List<ObjectIdentifier> objects = Arrays.stream(keys)
                .filter(Objects::nonNull)
                .filter(key -> !key.isBlank())
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();

        if (objects.isEmpty()) return;

        DeleteObjectsResponse response = s3Client
                .deleteObjects(DeleteObjectsRequest.builder()
                        .bucket(bucketName)
                        .delete(Delete.builder()
                                .objects(objects)
                                .quiet(false)
                                .build())
                        .build());

        if (response.hasErrors()) {
            String[] failedKeys = response.errors().stream()
                    .map(S3Error::key)
                    .toArray(String[]::new);
            deleteImages(remainingAttempts - 1, failedKeys);
        }
    }

    @Override
    public String getPublicUrl(String key) {
        return apiUrl + "/" + key;
    }

    @Override
    public void registerImageCleanup(String newImageKey, String oldImageKey) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new ApiException("No active transaction synchronization", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_COMMITTED) {
                            if (oldImageKey != null) {
                                deleteImages(3, oldImageKey);
                            }
                        } else if (status == STATUS_ROLLED_BACK) {
                            if (newImageKey != null) {
                                deleteImages(3, newImageKey);
                            }
                        }
                    }
                }
        );
    }

    // helpers
    private ClassInfo getClassInfo(Object entity) {
        String className = entity.getClass().getSimpleName().toLowerCase();
        Long id;
        try {
            Method method = entity.getClass().getDeclaredMethod("getId");
            id = (Long) method.invoke(entity);
        } catch (Exception e) {
            throw new ApiException("Illegal object passed / Error parsing entity info", HttpStatus.BAD_REQUEST);
        }
        return new ClassInfo(id, className);
    }
}
