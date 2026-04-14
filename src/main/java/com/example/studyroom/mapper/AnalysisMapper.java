package com.example.studyroom.mapper;

import com.example.studyroom.po.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 统计分析 Mapper
 * 集中管理所有统计类 SQL，避免在业务 Mapper 中混入统计逻辑
 */
@Mapper
public interface AnalysisMapper {

        // ─────────────────── Dashboard 总览 ─────────────────────────────────────────

        /** 用户总数 */
        @Select("SELECT COUNT(*) FROM sys_user")
        int countUsers();

        /** 自习室总数 */
        @Select("SELECT COUNT(*) FROM study_room")
        int countRooms();

        /** 座位总数 */
        @Select("SELECT COUNT(*) FROM seat")
        int countSeats();

        /** 当前占用中的座位数 */
        @Select("SELECT COUNT(*) FROM seat WHERE status = 'occupied'")
        int countOccupiedSeats();

        /** 今日预约数（按 start_time 日期判断） */
        @Select("SELECT COUNT(*) FROM reservation WHERE DATE(start_time) = CURDATE()")
        int countTodayReservations();

        /** 今日签到数 */
        @Select("SELECT COUNT(*) FROM check_in_record WHERE DATE(check_in_time) = CURDATE()")
        int countTodayCheckIns();

        /** 今日 AI 自动释放通知数 */
        @Select("SELECT COUNT(*) FROM notification WHERE type = 'seat_auto_released' AND DATE(send_time) = CURDATE()")
        int countTodayAutoReleases();

        /** 今日手机检测提醒数 */
        @Select("SELECT COUNT(*) FROM notification WHERE type = 'study_warning' AND DATE(send_time) = CURDATE()")
        int countTodayPhoneWarnings();

        // ─────────────────── 自习室座位状态 ──────────────────────────────────────────

        /** 某自习室的座位总数 */
        @Select("SELECT COUNT(*) FROM seat WHERE room_id = #{roomId}")
        int countSeatsByRoomId(Integer roomId);

        /** 某自习室指定状态的座位数 */
        @Select("SELECT COUNT(*) FROM seat WHERE room_id = #{roomId} AND status = #{status}")
        int countSeatsByRoomIdAndStatus(@Param("roomId") Integer roomId, @Param("status") String status);

        // ─────────────────── 按小时统计占用 ──────────────────────────────────────────

        /**
         * 统计某自习室在指定小时区间内的占用座位数
         * 预约覆盖条件：start_time < hourEnd AND end_time > hourStart
         * 只统计有效预约（reserved / completed）
         */
        @Select("SELECT COUNT(DISTINCT r.seat_id) FROM reservation r " +
                        "INNER JOIN seat s ON r.seat_id = s.seat_id " +
                        "WHERE s.room_id = #{roomId} " +
                        "AND r.start_time < #{hourEnd} AND r.end_time > #{hourStart} " +
                        "AND r.status IN ('reserved', 'completed')")
        int countOccupiedSeatsInHour(@Param("roomId") Integer roomId,
                        @Param("hourStart") java.time.LocalDateTime hourStart,
                        @Param("hourEnd") java.time.LocalDateTime hourEnd);

        // ─────────────────── 用户个人统计 ────────────────────────────────────────────

        /** 用户的预约总数 */
        @Select("SELECT COUNT(*) FROM reservation WHERE user_id = #{userId}")
        int countReservationsByUserId(Integer userId);

        /** 用户的签到总数 */
        @Select("SELECT COUNT(*) FROM check_in_record cr " +
                        "INNER JOIN reservation r ON cr.reservation_id = r.reservation_id " +
                        "WHERE r.user_id = #{userId}")
        int countCheckInsByUserId(Integer userId);

        /** 用户指定状态的预约数量 */
        @Select("SELECT COUNT(*) FROM reservation WHERE user_id = #{userId} AND status = #{status}")
        int countReservationsByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") String status);

        /** 用户被自动释放的次数 */
        @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND type = 'seat_auto_released'")
        int countAutoReleasesByUserId(Integer userId);

        /** 用户手机警告次数 */
        @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND type = 'study_warning'")
        int countPhoneWarningsByUserId(Integer userId);

        // ─────────────────── 行为检测统计 ────────────────────────────────────────────

        /** 某座位的手机检测次数 */
        @Select("SELECT COUNT(*) FROM behavior_detection WHERE seat_id = #{seatId} AND behavior_type = 'phone_detected'")
        int countPhoneDetectionsBySeatId(Integer seatId);

        /** 某用户关联的手机检测次数（通过预约关联座位） */
        @Select("SELECT COUNT(*) FROM behavior_detection bd " +
                        "INNER JOIN reservation r ON bd.seat_id = r.seat_id " +
                        "AND bd.detect_time BETWEEN r.start_time AND r.end_time " +
                        "WHERE r.user_id = #{userId} AND bd.behavior_type = 'phone_detected'")
        int countPhoneDetectionsByUserId(Integer userId);
}
