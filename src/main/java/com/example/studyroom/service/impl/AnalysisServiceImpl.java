package com.example.studyroom.service.impl;

import com.example.studyroom.constant.SystemConstants;
import com.example.studyroom.dto.*;
import com.example.studyroom.mapper.AnalysisMapper;
import com.example.studyroom.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计分析业务实现类
 */
@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private AnalysisMapper analysisMapper;

    // ─────────────────── 接口 1：系统总览 ────────────────────────────────────────

    @Override
    public DashboardDTO getDashboard() {
        DashboardDTO dto = new DashboardDTO();
        dto.setUserCount(analysisMapper.countUsers());
        dto.setRoomCount(analysisMapper.countRooms());
        dto.setSeatCount(analysisMapper.countSeats());
        dto.setOccupiedCount(analysisMapper.countOccupiedSeats());
        dto.setTodayReservationCount(analysisMapper.countTodayReservations());
        dto.setTodayCheckInCount(analysisMapper.countTodayCheckIns());
        dto.setTodayAutoReleaseCount(analysisMapper.countTodayAutoReleases());
        dto.setTodayPhoneWarningCount(analysisMapper.countTodayPhoneWarnings());
        return dto;
    }

    // ─────────────────── 接口 2：自习室当前状态 ──────────────────────────────────

    @Override
    public RoomStatusDTO getRoomCurrentStatus(Integer roomId) {
        RoomStatusDTO dto = new RoomStatusDTO();

        int total = analysisMapper.countSeatsByRoomId(roomId);
        int available = analysisMapper.countSeatsByRoomIdAndStatus(roomId, SystemConstants.SeatStatus.AVAILABLE);
        int reserved = analysisMapper.countSeatsByRoomIdAndStatus(roomId, SystemConstants.SeatStatus.RESERVED);
        int occupied = analysisMapper.countSeatsByRoomIdAndStatus(roomId, SystemConstants.SeatStatus.OCCUPIED);

        dto.setTotal(total);
        dto.setAvailable(available);
        dto.setReserved(reserved);
        dto.setOccupied(occupied);

        // 使用率 = (reserved + occupied) / total
        if (total > 0) {
            BigDecimal usage = new BigDecimal(reserved + occupied)
                    .divide(new BigDecimal(total), 2, RoundingMode.HALF_UP);
            dto.setUsageRate(usage);
        } else {
            dto.setUsageRate(BigDecimal.ZERO);
        }

        return dto;
    }

    // ─────────────────── 接口 3：按小时统计占用率 ────────────────────────────────

    @Override
    public List<HourlyUsageDTO> getRoomHourlyUsage(Integer roomId) {
        List<HourlyUsageDTO> result = new ArrayList<>(24);
        LocalDate today = LocalDate.now();
        int totalSeats = analysisMapper.countSeatsByRoomId(roomId);

        // 必须循环输出 24 个小时，保证前端可以直接画图
        for (int h = 0; h < 24; h++) {
            LocalDateTime hourStart = LocalDateTime.of(today, LocalTime.of(h, 0));
            LocalDateTime hourEnd = hourStart.plusHours(1);

            BigDecimal usageRate;
            if (totalSeats > 0) {
                int occupiedInHour = analysisMapper.countOccupiedSeatsInHour(roomId, hourStart, hourEnd);
                usageRate = new BigDecimal(occupiedInHour)
                        .divide(new BigDecimal(totalSeats), 2, RoundingMode.HALF_UP);
            } else {
                usageRate = BigDecimal.ZERO;
            }

            result.add(new HourlyUsageDTO(h, usageRate));
        }

        return result;
    }

    // ─────────────────── 接口 4：用户个人统计 ────────────────────────────────────

    @Override
    public UserStatsDTO getUserStats(Integer userId) {
        UserStatsDTO dto = new UserStatsDTO();
        dto.setReservationCount(analysisMapper.countReservationsByUserId(userId));
        dto.setCheckInCount(analysisMapper.countCheckInsByUserId(userId));
        dto.setCompletedCount(
                analysisMapper.countReservationsByUserIdAndStatus(userId, SystemConstants.ReservationStatus.COMPLETED));
        dto.setCancelledCount(
                analysisMapper.countReservationsByUserIdAndStatus(userId, SystemConstants.ReservationStatus.CANCELLED));
        dto.setAutoReleaseCount(analysisMapper.countAutoReleasesByUserId(userId));
        dto.setPhoneWarningCount(analysisMapper.countPhoneWarningsByUserId(userId));
        return dto;
    }

    // ─────────────────── 接口 5：行为检测统计 ────────────────────────────────────

    @Override
    public BehaviorStatsDTO getBehaviorStatsBySeatId(Integer seatId) {
        BehaviorStatsDTO dto = new BehaviorStatsDTO();
        dto.setPhoneDetectionCount(analysisMapper.countPhoneDetectionsBySeatId(seatId));
        return dto;
    }

    @Override
    public BehaviorStatsDTO getBehaviorStatsByUserId(Integer userId) {
        BehaviorStatsDTO dto = new BehaviorStatsDTO();
        dto.setPhoneDetectionCount(analysisMapper.countPhoneDetectionsByUserId(userId));
        return dto;
    }
}
