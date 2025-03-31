package com.Lvtu.mapper;

import com.Lvtu.entity.ScenicSpot;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import java.util.List;

// ScenicSpotMapper.java
@Mapper
public interface ScenicSpotMapper extends BaseMapper<ScenicSpot> {
    @Select("SELECT * FROM scenic_spots ORDER BY create_time DESC LIMIT #{limit}")
    List<ScenicSpot> selectWithLimit(@Param("limit") int limit);
}

