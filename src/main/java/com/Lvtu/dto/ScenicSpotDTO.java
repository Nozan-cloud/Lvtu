package com.Lvtu.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// ScenicSpotDTO.java
@Data
public class ScenicSpotDTO {
    private Long id;
    private String title;
    private String description;
    private String content;
    private String location;
    private List<String> pictures;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}