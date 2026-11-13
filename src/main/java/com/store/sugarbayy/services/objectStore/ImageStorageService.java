package com.store.sugarbayy.services.objectStore;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStorageService {
    List<String> uploadImages(Object parentEntity, MultipartFile... files);
    void deleteImages(int remainingAttempts, String... keys);
    void registerImageCleanup(List<String> newImageKeys, List<String> oldImageKeys);
}
