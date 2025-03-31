// FoodServiceImpl.java
package com.Lvtu.service.impl;

import com.Lvtu.dto.FoodDTO;
import com.Lvtu.entity.Food;
import com.Lvtu.mapper.FoodMapper;
import com.Lvtu.service.FoodImageService;
import com.Lvtu.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {
    private final FoodMapper foodMapper;
    private final FoodImageService foodImageService;

    @Override
    public List<FoodDTO> getFoodsWithLimit(int limit) {
        List<Food> foods = foodMapper.selectWithLimit(limit);
        return foods.stream().map(food -> {
            FoodDTO dto = new FoodDTO();
            BeanUtils.copyProperties(food, dto);
            dto.setPictures(foodImageService.getImageUrlsByFoodId(food.getId()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public FoodDTO getFoodById(Long id) {
        Food food = foodMapper.selectById(id);
        if (food == null) {
            return null;
        }
        FoodDTO dto = new FoodDTO();
        BeanUtils.copyProperties(food, dto);
        dto.setPictures(foodImageService.getImageUrlsByFoodId(id));
        return dto;
    }
}
