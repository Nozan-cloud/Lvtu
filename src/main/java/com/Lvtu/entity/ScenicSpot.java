package com.Lvtu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// ScenicSpot.java
@Data
@TableName("scenic_spots")
public class ScenicSpot {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String content;
    private String location;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}