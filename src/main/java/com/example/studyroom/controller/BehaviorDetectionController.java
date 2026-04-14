package com.example.studyroom.controller;

import com.example.studyroom.po.BehaviorDetection;
import com.example.studyroom.service.BehaviorDetectionService;
import com.example.studyroom.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行为检测 Controller
 */
@RestController
@RequestMapping("/behavior")
public class BehaviorDetectionController {

    @Autowired
    private BehaviorDetectionService behaviorDetectionService;

    /**
     * AI 模块上传行为检测结果
     * POST /behavior/upload
     */
    @PostMapping("/upload")
    public Result<BehaviorDetection> upload(@RequestBody BehaviorDetection detection) {
        return Result.success(behaviorDetectionService.upload(detection));
    }

    /**
     * 查询座位的行为检测记录
     * GET /behavior/seat/{seatId}
     */
    @GetMapping("/seat/{seatId}")
    public Result<List<BehaviorDetection>> getBySeatId(@PathVariable("seatId") Integer seatId) {
        return Result.success(behaviorDetectionService.getBySeatId(seatId));
    }
}
