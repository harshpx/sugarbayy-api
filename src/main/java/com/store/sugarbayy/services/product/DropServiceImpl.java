package com.store.sugarbayy.services.product;

import com.store.sugarbayy.configurations.Common;
import com.store.sugarbayy.configurations.exceptions.ApiException;
import com.store.sugarbayy.dtos.product.DropInfo;
import com.store.sugarbayy.dtos.product.DropModifyRequest;
import com.store.sugarbayy.entities.ProductDrop;
import com.store.sugarbayy.repos.ProductDropRepo;
import com.store.sugarbayy.services.objectStore.ImageStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DropServiceImpl implements DropService {
    private final ProductDropRepo dropRepo;
    private final ImageStorageService imageStorageService;

    @Override
    public List<DropInfo> listDrops() {
        return dropRepo.fetchDrops();
    }

    @Override
    @Transactional
    public DropInfo createDrop(DropModifyRequest request) {
        if (dropRepo.existsByNameIgnoreCase(request.name())) {
            throw new ApiException("Drop with name: " + request.name().toLowerCase() + " already exists", HttpStatus.CONFLICT);
        }

        String cleanedName = Common.toSentenceCase(request.name());
        String cleanedDescription = Common.toSentenceCase(request.description());

        ProductDrop drop = ProductDrop.builder()
                .name(cleanedName)
                .description(cleanedDescription)
                .build();

        drop = dropRepo.save(drop);

        if (request.coverPhoto() != null && !request.coverPhoto().isEmpty()) {
            String imageKey = imageStorageService.uploadImage(request.coverPhoto(), drop);
            drop.setCoverPhoto(imageKey);

            imageStorageService.registerImageCleanup(imageKey, null);
        }
        return DropInfo.fromEntity(drop);
    }

    @Override
    @Transactional
    public DropInfo updateDrop(DropModifyRequest request, Long dropId) {
        ProductDrop existingDrop = dropRepo.findById(dropId)
                .orElseThrow(() -> new ApiException("Product drop does not exist", HttpStatus.NOT_FOUND));

        String cleanedName = Common.toSentenceCase(request.name());
        String cleanedDescription = Common.toSentenceCase(request.description());

        existingDrop.setName(cleanedName);
        existingDrop.setDescription(cleanedDescription);

        MultipartFile newPhoto = request.coverPhoto();

        if (newPhoto != null && !newPhoto.isEmpty()) {
            String oldImageKey = existingDrop.getCoverPhoto();
            String newImageKey = imageStorageService.uploadImage(request.coverPhoto(), existingDrop);
            existingDrop.setCoverPhoto(newImageKey);
            imageStorageService.registerImageCleanup(newImageKey, oldImageKey);
        }

        return DropInfo.fromEntity(existingDrop);
    }

    @Override
    @Transactional
    public int deleteDrop(Long dropId) {
        ProductDrop existingDrop = dropRepo.findById(dropId).orElse(null);
        if (existingDrop == null) return 0;
        String imageKey = existingDrop.getCoverPhoto();
        dropRepo.delete(existingDrop);
        if (imageKey != null) {
            imageStorageService.registerImageCleanup(null, imageKey);
        }
        return 1;
    }
}
