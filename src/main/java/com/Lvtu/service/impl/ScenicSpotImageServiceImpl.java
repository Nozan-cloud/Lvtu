package com.Lvtu.service.impl;

import com.Lvtu.entity.ScenicSpotImage;
import com.Lvtu.mapper.ScenicSpotImageMapper;
import com.Lvtu.service.ScenicSpotImageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ScenicSpotImageServiceImpl.java
@Service
@RequiredArgsConstructor
public class ScenicSpotImageServiceImpl implements ScenicSpotImageService {
    private final ScenicSpotImageMapper imageMapper;

    @Override
    public List<String> getImageUrlsBySpotId(Long spotId) {
        return imageMapper.selectImageUrlsBySpotId(spotId);
    }

    @Override
    @Transactional
    public void saveImages(Long spotId, List<String> imageUrls) {
        // 先删除旧图片
        imageMapper.delete(new LambdaQueryWrapper<ScenicSpotImage>()
                .eq(ScenicSpotImage::getSpotId, spotId));

        // 批量插入新图片
        if (!imageUrls.isEmpty()) {
            imageMapper.batchInsertImages(spotId, imageUrls);
        }
    }
}