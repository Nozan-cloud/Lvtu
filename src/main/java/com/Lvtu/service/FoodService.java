// FoodService.java
package com.Lvtu.service;

import com.Lvtu.dto.FoodDTO;
import java.util.List;

public interface FoodService {
    List<FoodDTO> getFoodsWithLimit(int limit);
    FoodDTO getFoodById(Long id);
}