package com.Lvtu.service;

import com.Lvtu.dto.ScenicSpotDTO;

import java.util.List;

// ScenicSpotService.java
public interface ScenicSpotService {
    List<ScenicSpotDTO> getSpotsWithLimit(int limit);
    ScenicSpotDTO getSpotById(Long id);
}