
// FoodImage.java
package com.Lvtu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("food_images")
public class FoodImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long foodId;
    private String imageUrl;
    private Integer sortOrder;
    private LocalDateTime createTime;
}