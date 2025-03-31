// FoodImageService.java
package com.Lvtu.service;

import java.util.List;

public interface FoodImageService {
    List<String> getImageUrlsByFoodId(Long foodId);
    void saveImages(Long foodId, List<String> imageUrls);
}