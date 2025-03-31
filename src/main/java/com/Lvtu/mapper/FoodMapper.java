package com.Lvtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.Lvtu.entity.Food;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FoodMapper extends BaseMapper<Food> {
    @Select("SELECT * FROM foods ORDER BY create_time DESC LIMIT #{limit}")
    List<Food> selectWithLimit(@Param("limit") int limit);
}