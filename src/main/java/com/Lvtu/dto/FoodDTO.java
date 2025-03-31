// FoodDTO.java
package com.Lvtu.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FoodDTO {
    private Long id;
    private String title;
    private String description;
    private String content;
    private String location;
    private String priceRange;
    private List<String> pictures;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}