package com.Lvtu.service;

import java.util.List;

// ScenicSpotImageService.java
public interface ScenicSpotImageService {
    List<String> getImageUrlsBySpotId(Long spotId);
    void saveImages(Long spotId, List<String> imageUrls);
}