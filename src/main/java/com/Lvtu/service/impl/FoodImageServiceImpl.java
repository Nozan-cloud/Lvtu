// FoodImageServiceImpl.java
package com.Lvtu.service.impl;

import com.Lvtu.entity.FoodImage;
import com.Lvtu.mapper.FoodImageMapper;
import com.Lvtu.service.FoodImageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodImageServiceImpl implements FoodImageService {
    private final FoodImageMapper foodImageMapper;

    @Override
    public List<String> getImageUrlsByFoodId(Long foodId) {
        return foodImageMapper.selectImageUrlsByFoodId(foodId);
    }

    @Override
    @Transactional
    public void saveImages(Long foodId, List<String> imageUrls) {
        // 先删除旧图片
        foodImageMapper.delete(new LambdaQueryWrapper<FoodImage>()
                .eq(FoodImage::getFoodId, foodId));

        // 批量插入新图片
        if (!imageUrls.isEmpty()) {
            foodImageMapper.batchInsertImages(foodId, imageUrls);
        }
    }
}