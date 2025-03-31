// FoodController.java
package com.Lvtu.controller;

import com.Lvtu.dto.FoodDTO;
import com.Lvtu.service.FoodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foods")
@RequiredArgsConstructor
@Api(tags = "美食管理")
public class FoodController {
    private final FoodService foodService;

    @GetMapping
    @ApiOperation("获取美食列表")
    public ResponseEntity<List<FoodDTO>> getFoods(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(foodService.getFoodsWithLimit(limit));
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID获取美食详情")
    public ResponseEntity<FoodDTO> getFoodById(@PathVariable Long id) {
        FoodDTO food = foodService.getFoodById(id);
        return food != null ? ResponseEntity.ok(food) : ResponseEntity.notFound().build();
    }
}