package com.example.studyroom.service;

import com.example.studyroom.dto.*;

import java.util.List;

/**
 * 统计分析业务接口
 */
public interface AnalysisService {

    /** 系统总览 */
    DashboardDTO getDashboard();

    /** 自习室当前座位状态 */
    RoomStatusDTO getRoomCurrentStatus(Integer roomId);

    /** 自习室按小时占用率（今日，0-23 小时） */
    List<HourlyUsageDTO> getRoomHourlyUsage(Integer roomId);

    /** 用户个人使用统计 */
    UserStatsDTO getUserStats(Integer userId);

    /** 座位行为检测统计 */
    BehaviorStatsDTO getBehaviorStatsBySeatId(Integer seatId);

    /** 用户行为检测统计 */
    BehaviorStatsDTO getBehaviorStatsByUserId(Integer userId);
}
