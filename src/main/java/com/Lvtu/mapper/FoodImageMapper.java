// FoodImageMapper.java
package com.Lvtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.Lvtu.entity.FoodImage;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FoodImageMapper extends BaseMapper<FoodImage> {
    @Select("SELECT image_url FROM food_images WHERE food_id = #{foodId} ORDER BY sort_order")
    List<String> selectImageUrlsByFoodId(Long foodId);

    @Insert("<script>" +
            "INSERT INTO food_images (food_id, image_url, sort_order) VALUES " +
            "<foreach collection='urls' item='url' index='index' separator=','>" +
            "(#{foodId}, #{url}, #{index})" +
            "</foreach>" +
            "</script>")
    int batchInsertImages(@Param("foodId") Long foodId, @Param("urls") List<String> urls);
}