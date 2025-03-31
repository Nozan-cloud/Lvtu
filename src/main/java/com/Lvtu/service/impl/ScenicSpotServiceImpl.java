package com.Lvtu.service.impl;

import com.Lvtu.dto.ScenicSpotDTO;
import com.Lvtu.entity.ScenicSpot;
import com.Lvtu.mapper.ScenicSpotMapper;
import com.Lvtu.service.ScenicSpotImageService;
import com.Lvtu.service.ScenicSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScenicSpotServiceImpl implements ScenicSpotService {
    private final ScenicSpotMapper spotMapper;
    private final ScenicSpotImageService imageService;

    @Override
    public List<ScenicSpotDTO> getSpotsWithLimit(int limit) {
        List<ScenicSpot> spots = spotMapper.selectWithLimit(limit);
        return spots.stream().map(spot -> {
            ScenicSpotDTO dto = new ScenicSpotDTO();
            BeanUtils.copyProperties(spot, dto);
            dto.setPictures(imageService.getImageUrlsBySpotId(spot.getId()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public ScenicSpotDTO getSpotById(Long id) {
        ScenicSpot spot = spotMapper.selectById(id);
        if (spot == null) {
            return null;
        }
        ScenicSpotDTO dto = new ScenicSpotDTO();
        BeanUtils.copyProperties(spot, dto);
        dto.setPictures(imageService.getImageUrlsBySpotId(id));
        return dto;
    }
}
