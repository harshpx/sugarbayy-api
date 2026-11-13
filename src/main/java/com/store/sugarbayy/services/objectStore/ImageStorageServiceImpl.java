package com.store.sugarbayy.services.objectStore;

import com.store.sugarbayy.configurations.exceptions.ApiException;
import com.store.sugarbayy.configurations.Common;
import com.store.sugarbayy.dtos.common.ClassInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ImageStorageServiceImpl implements ImageStorageService {
    private final S3Client s3Client;
    private final String bucketName = "sugarbayy";

    public ImageStorageServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public List<String> uploadImages(Object parentEntity, MultipartFile... files) {
        if (files == null || files.length == 0) {
            throw new ApiException("Images must not be empty", HttpStatus.BAD_REQUEST);
        }

        ClassInfo parentEntityInfo = getClassInfo(parentEntity);

        List<String> uploadedKeys = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    throw new ApiException("Image can not be null or empty", HttpStatus.BAD_REQUEST);
                }

                String imageExtension = getValidImageExtensionFromFile(file);

                String key = parentEntityInfo.name() + "/" +
                        parentEntityInfo.id() + "/" +
                        Common.generateRandomString(6) + imageExtension;

                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(file.getContentType())
                        .build();

                s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

                uploadedKeys.add(key);
            }
        } catch (Exception e) {
            log.error("Failed to bulk upload images to R2. Uploaded keys: {} (will be deleted in cleanup)",
                    uploadedKeys, e);
            deleteImages(4, uploadedKeys.toArray(String[]::new));
            if (e instanceof ApiException apiException) {
                throw apiException;
            }
            throw new ApiException("Unable to bulk upload images", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return uploadedKeys;
    }

    @Override
    public void deleteImages(int remainingAttempts, String... keys) {
        if (remainingAttempts <= 0 || keys == null || keys.length == 0) {
            return;
        }
        List<ObjectIdentifier> objects = Arrays.stream(keys)
                .filter(Objects::nonNull)
                .filter(key -> !key.isBlank())
                .distinct()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();

        if (objects.isEmpty()) return;

        try {
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
        } catch (Exception e) {
            log.error("Failed to delete images from R2. Keys: {}", keys, e);
            deleteImages(remainingAttempts - 1, keys);
        }
    }

    @Override
    public void registerImageCleanup(List<String> newImageKeys, List<String> oldImageKeys) {
        // JPA Transaction boundary check
        // throws error if invoking method is not marked with @Transactional
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new ApiException("No active transaction synchronization", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_COMMITTED) {
                            if (oldImageKeys != null && !oldImageKeys.isEmpty()) {
                                deleteImages(3, oldImageKeys.toArray(String[]::new));
                            }
                        } else if (status == STATUS_ROLLED_BACK) {
                            if (newImageKeys != null && !newImageKeys.isEmpty()) {
                                deleteImages(3, newImageKeys.toArray(String[]::new));
                            }
                        }
                    }
                }
        );
    }

    // helpers
    private ClassInfo getClassInfo(Object entity) {
        if (entity == null) {
            throw new ApiException("Entity can not be null", HttpStatus.BAD_REQUEST);
        }
        String className = entity.getClass().getSimpleName().toLowerCase();
        long id;
        try {
            Method method = entity.getClass().getDeclaredMethod("getId");
            id = ((Number) method.invoke(entity)).longValue();
        } catch (Exception e) {
            throw new ApiException("Illegal object passed / Error parsing entity info", HttpStatus.BAD_REQUEST);
        }
        return new ClassInfo(id, className);
    }

    private String getValidImageExtensionFromFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new ApiException("Image content type is missing", HttpStatus.BAD_REQUEST);
        }
        return switch (contentType) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/heic" -> ".heic";
            case "image/heif" -> ".heif";
            case "image/avif" -> ".avif";
            case "image/tiff", "image/tif" -> ".tiff";
            default -> throw new ApiException("Unsupported Image Type: " + contentType, HttpStatus.BAD_REQUEST);
        };
    }
}
