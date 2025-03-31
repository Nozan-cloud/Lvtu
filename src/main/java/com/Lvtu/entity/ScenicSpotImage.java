package com.Lvtu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

// ScenicSpotImage.java
@Data
@TableName("scenic_spot_images")
public class ScenicSpotImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long spotId;
    private String imageUrl;
    private Integer sortOrder;
    private LocalDateTime createTime;
}