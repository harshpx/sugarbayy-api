package com.store.sugarbayy.services.objectStore;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    String uploadImage(MultipartFile file, Object parentEntity);
    void deleteImages(int remainingAttempts, String... keys);
    String getPublicUrl(String key);
    public void registerImageCleanup(String newImageKey, String oldImageKey);
}
