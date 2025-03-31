package com.Lvtu.mapper;


import com.Lvtu.entity.ScenicSpotImage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import java.util.List;
// ScenicSpotImageMapper.java
@Mapper
public interface ScenicSpotImageMapper extends BaseMapper<ScenicSpotImage> {
    @Select("SELECT image_url FROM scenic_spot_images WHERE spot_id = #{spotId} ORDER BY sort_order")
    List<String> selectImageUrlsBySpotId(Long spotId);

    @Insert("<script>" +
            "INSERT INTO scenic_spot_images (spot_id, image_url, sort_order) VALUES " +
            "<foreach collection='urls' item='url' index='index' separator=','>" +
            "(#{spotId}, #{url}, #{index})" +
            "</foreach>" +
            "</script>")
    int batchInsertImages(@Param("spotId") Long spotId, @Param("urls") List<String> urls);
}