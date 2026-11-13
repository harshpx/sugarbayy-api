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
        // check duplicate name
        if (dropRepo.existsByNameIgnoreCase(request.name())) {
            throw new ApiException("Drop with name: " + request.name().toLowerCase() + " already exists", HttpStatus.CONFLICT);
        }

        // build new drop without multipart content keys & persist
        ProductDrop drop = ProductDrop.builder()
                .name(Common.toSentenceCase(request.name()))
                .description(Common.toSentenceCase(request.description()))
                .build();

        drop = dropRepo.save(drop);

        // handle multipart files upload & set keys to drop
        if (request.coverPhoto() != null && !request.coverPhoto().isEmpty()) {
            List<String> imageKey = imageStorageService.uploadImages(drop, request.coverPhoto());
            imageStorageService.registerImageCleanup(imageKey, null);
            drop.setCoverPhoto(imageKey.getFirst());
        }

        return DropInfo.fromEntity(drop);
    }

    @Override
    @Transactional
    public DropInfo updateDrop(DropModifyRequest request, Long dropId) {
        // check existing
        ProductDrop existingDrop = dropRepo.findById(dropId)
                .orElseThrow(() -> new ApiException("Product drop does not exist", HttpStatus.NOT_FOUND));

        // update all fields except multipart ones
        existingDrop.setName(Common.toSentenceCase(request.name()));
        existingDrop.setDescription(Common.toSentenceCase(request.description()));

        // handle multipart files update
        MultipartFile newPhoto = request.coverPhoto();
        String oldImageKey = existingDrop.getCoverPhoto();

        if (newPhoto != null && !newPhoto.isEmpty()) {
            List<String> newImageKey = imageStorageService.uploadImages(existingDrop, request.coverPhoto());
            imageStorageService.registerImageCleanup(newImageKey, oldImageKey == null ? null : List.of(oldImageKey));
            existingDrop.setCoverPhoto(newImageKey.getFirst());
        } else if (Boolean.TRUE.equals(request.deleteCoverPhoto())) {
            imageStorageService.registerImageCleanup(null, oldImageKey == null ? null : List.of(oldImageKey));
            existingDrop.setCoverPhoto(null);
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
            imageStorageService.registerImageCleanup(null, List.of(imageKey));
        }
        return 1;
    }
}
