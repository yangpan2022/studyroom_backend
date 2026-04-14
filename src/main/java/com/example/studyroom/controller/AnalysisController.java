package com.example.studyroom.controller;

import com.example.studyroom.dto.*;
import com.example.studyroom.service.AnalysisService;
import com.example.studyroom.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统计分析 Controller
 * 基础路径：/analysis
 */
@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    /**
     * 接口 1：系统总览
     * GET /analysis/dashboard
     */
    @GetMapping("/dashboard")
    public Result<DashboardDTO> dashboard() {
        return Result.success(analysisService.getDashboard());
    }

    /**
     * 接口 2：自习室当前状态
     * GET /analysis/room/{roomId}/current
     */
    @GetMapping("/room/{roomId}/current")
    public Result<RoomStatusDTO> roomCurrentStatus(@PathVariable("roomId") Integer roomId) {
        return Result.success(analysisService.getRoomCurrentStatus(roomId));
    }

    /**
     * 接口 3：自习室按小时占用率
     * GET /analysis/room/{roomId}/hourly
     */
    @GetMapping("/room/{roomId}/hourly")
    public Result<List<HourlyUsageDTO>> roomHourlyUsage(@PathVariable("roomId") Integer roomId) {
        return Result.success(analysisService.getRoomHourlyUsage(roomId));
    }

    /**
     * 接口 4：用户个人使用统计
     * GET /analysis/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public Result<UserStatsDTO> userStats(@PathVariable("userId") Integer userId) {
        return Result.success(analysisService.getUserStats(userId));
    }

    /**
     * 接口 5a：座位行为检测统计
     * GET /analysis/behavior/seat/{seatId}
     */
    @GetMapping("/behavior/seat/{seatId}")
    public Result<BehaviorStatsDTO> behaviorStatsBySeat(@PathVariable("seatId") Integer seatId) {
        return Result.success(analysisService.getBehaviorStatsBySeatId(seatId));
    }

    /**
     * 接口 5b：用户行为检测统计
     * GET /analysis/behavior/user/{userId}
     */
    @GetMapping("/behavior/user/{userId}")
    public Result<BehaviorStatsDTO> behaviorStatsByUser(@PathVariable("userId") Integer userId) {
        return Result.success(analysisService.getBehaviorStatsByUserId(userId));
    }
}
