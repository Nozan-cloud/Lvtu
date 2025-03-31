package com.Lvtu.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Lvtu.dto.ScenicSpotDTO;
import com.Lvtu.service.ScenicSpotService;
import java.util.List;


// ScenicSpotController.java
@RestController
@RequestMapping("/scenic-spots")
@RequiredArgsConstructor
@Api(tags = "景点管理")
public class ScenicSpotController {
    private final ScenicSpotService spotService;

    @GetMapping
    public ResponseEntity<List<ScenicSpotDTO>> getSpots(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(spotService.getSpotsWithLimit(limit));
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID获取景点信息")
    public ResponseEntity<ScenicSpotDTO> getSpotById(@PathVariable Long id) {
        ScenicSpotDTO spot = spotService.getSpotById(id);
        return spot != null ? ResponseEntity.ok(spot) : ResponseEntity.notFound().build();
    }
}