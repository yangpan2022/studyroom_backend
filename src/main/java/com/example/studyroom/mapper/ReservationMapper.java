package com.example.studyroom.mapper;

import com.example.studyroom.po.Reservation;
import com.example.studyroom.vo.ReservationVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 预约 Mapper 接口（MyBatis 注解方式）
 */
@Mapper
public interface ReservationMapper {

        // ─── 原有 PO 查询（用于内部逻辑：冲突检测、状态流转等）─────────────────────

        @Select("SELECT * FROM reservation")
        List<Reservation> findAll();

        @Select("SELECT * FROM reservation WHERE reservation_id = #{reservationId}")
        Reservation findById(Integer reservationId);

        /** 根据用户 ID 查询该用户的所有预约 */
        @Select("SELECT * FROM reservation WHERE user_id = #{userId} ORDER BY start_time DESC")
        List<Reservation> findByUserId(Integer userId);

        /** 根据座位 ID 查询该座位的所有预约 */
        @Select("SELECT * FROM reservation WHERE seat_id = #{seatId} ORDER BY start_time DESC")
        List<Reservation> findBySeatId(Integer seatId);

        /**
         * 查询座位在指定时间段内的有效预约（用于冲突检测）
         * 有效状态：reserved（已预约未完成）
         */
        @Select("SELECT * FROM reservation WHERE seat_id = #{seatId} " +
                        "AND status = 'reserved' " +
                        "AND start_time < #{endTime} AND end_time > #{startTime}")
        List<Reservation> findConflictReservations(@Param("seatId") Integer seatId,
                        @Param("startTime") java.time.LocalDateTime startTime,
                        @Param("endTime") java.time.LocalDateTime endTime);

        /**
         * 查询排除某自身的冲突预约（用于修改预约时判定）
         */
        @Select("SELECT * FROM reservation WHERE seat_id = #{seatId} " +
                        "AND status = 'reserved' " +
                        "AND reservation_id != #{excludeReservationId} " +
                        "AND start_time < #{endTime} AND end_time > #{startTime}")
        List<Reservation> findConflictReservationsExcluding(@Param("seatId") Integer seatId,
                        @Param("startTime") java.time.LocalDateTime startTime,
                        @Param("endTime") java.time.LocalDateTime endTime,
                        @Param("excludeReservationId") Integer excludeReservationId);

        @Insert("INSERT INTO reservation(user_id, seat_id, start_time, end_time, status) " +
                        "VALUES(#{userId}, #{seatId}, #{startTime}, #{endTime}, #{status})")
        @Options(useGeneratedKeys = true, keyProperty = "reservationId")
        int insert(Reservation reservation);

        /** 更新预约状态 */
        @Update("UPDATE reservation SET status=#{status} WHERE reservation_id=#{reservationId}")
        int updateStatus(@Param("reservationId") Integer reservationId, @Param("status") String status);

        /** 修改预约核心信息 */
        @Update("UPDATE reservation SET seat_id=#{seatId}, start_time=#{startTime}, end_time=#{endTime} WHERE reservation_id=#{reservationId}")
        int updateReservationProcess(@Param("reservationId") Integer reservationId, @Param("seatId") Integer seatId,
                        @Param("startTime") java.time.LocalDateTime startTime, @Param("endTime") java.time.LocalDateTime endTime);

        /**
         * 根据座位 ID 查询当前正在使用的有效预约
         * 兼容 reserved（已预约未签到）和 occupied（已签到使用中）两种活跃状态
         * 增加 NOW() 时间过滤，只返回当前时间在 [start_time, end_time] 区间内的预约
         * 用于：AI 自动释放 / 手机检测通知 精确定位用户
         */
        @Select("SELECT * FROM reservation WHERE seat_id = #{seatId} " +
                        "AND status IN ('reserved', 'occupied') " +
                        "ORDER BY start_time ASC LIMIT 1")
        Reservation findActiveReservationBySeatId(Integer seatId);

        @Delete("DELETE FROM reservation WHERE reservation_id = #{reservationId}")
        int deleteById(Integer reservationId);

        /**
         * 查找指定状态且 start_time 在 [from, until] 区间内的预约
         * 用于：预约即将开始提醒定时任务
         */
        @Select("SELECT * FROM reservation WHERE status = #{status} " +
                        "AND start_time >= #{from} AND start_time <= #{until}")
        List<Reservation> findByStatusAndStartTimeBetween(
                        @Param("status") String status,
                        @Param("from")   java.time.LocalDateTime from,
                        @Param("until")  java.time.LocalDateTime until);

        /**
         * 查找指定状态且 end_time 在 [from, until] 区间内的预约
         * 用于：预约即将结束提醒定时任务
         */
        @Select("SELECT * FROM reservation WHERE status = #{status} " +
                        "AND end_time >= #{from} AND end_time <= #{until}")
        List<Reservation> findByStatusAndEndTimeBetween(
                        @Param("status") String status,
                        @Param("from")   java.time.LocalDateTime from,
                        @Param("until")  java.time.LocalDateTime until);

        /**
         * 查询所有已过期（end_time < NOW()）且状态仍为 occupied 的预约
         * 用于：自动签退定时任务
         * 去重由状态保证：处理完成后 status 变为 completed，不会二次命中
         */
        @Select("SELECT * FROM reservation WHERE status = 'occupied' AND end_time < NOW()")
        List<Reservation> findExpiredOccupied();

        // ─── 用户统计专用查询 ─────────────────────────
        
        @Select("SELECT COUNT(1) FROM reservation WHERE user_id = #{userId}")
        int countByUserId(Integer userId);

        /** 查询用户有多少个未完成的活跃预约（含 reserved, occupied） */
        @Select("SELECT COUNT(1) FROM reservation WHERE user_id = #{userId} AND status IN ('reserved', 'occupied')")
        int countActiveByUserId(Integer userId);

        @Select("SELECT COUNT(1) FROM reservation WHERE user_id = #{userId} AND status = #{status}")
        int countByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") String status);

        @Select("SELECT SUM(TIMESTAMPDIFF(MINUTE, start_time, end_time)) FROM reservation WHERE user_id = #{userId} AND status = 'completed'")
        Integer sumCompletedStudyMinutes(Integer userId);

        // ─── VO 查询（用于前端展示，JOIN seat + study_room）─────────────────────────

        /** 查询全部预约（含位置信息） */
        @Select("SELECT r.reservation_id AS reservationId, r.user_id AS userId, u.username AS username, r.seat_id AS seatId, " +
                "r.start_time AS startTime, r.end_time AS endTime, r.status AS status, " +
                "s.seat_number AS seatNumber, sr.room_name AS roomName, sr.location AS location " +
                "FROM reservation r " +
                "LEFT JOIN sys_user u ON r.user_id = u.user_id " +
                "LEFT JOIN seat s ON r.seat_id = s.seat_id " +
                "LEFT JOIN study_room sr ON s.room_id = sr.room_id " +
                "ORDER BY r.start_time DESC")
        List<ReservationVO> findAllVO();

        /** 根据 reservationId 查询详情（含位置信息） */
        @Select("SELECT r.reservation_id AS reservationId, r.user_id AS userId, u.username AS username, r.seat_id AS seatId, " +
                "r.start_time AS startTime, r.end_time AS endTime, r.status AS status, " +
                "s.seat_number AS seatNumber, sr.room_name AS roomName, sr.location AS location " +
                "FROM reservation r " +
                "LEFT JOIN sys_user u ON r.user_id = u.user_id " +
                "LEFT JOIN seat s ON r.seat_id = s.seat_id " +
                "LEFT JOIN study_room sr ON s.room_id = sr.room_id " +
                "WHERE r.reservation_id = #{reservationId}")
        ReservationVO findVOById(Integer reservationId);

        /** 根据 userId 查询该用户的预约列表（含位置信息） */
        @Select("SELECT r.reservation_id AS reservationId, r.user_id AS userId, u.username AS username, r.seat_id AS seatId, " +
                "r.start_time AS startTime, r.end_time AS endTime, r.status AS status, " +
                "s.seat_number AS seatNumber, sr.room_name AS roomName, sr.location AS location " +
                "FROM reservation r " +
                "LEFT JOIN sys_user u ON r.user_id = u.user_id " +
                "LEFT JOIN seat s ON r.seat_id = s.seat_id " +
                "LEFT JOIN study_room sr ON s.room_id = sr.room_id " +
                "WHERE r.user_id = #{userId} ORDER BY r.start_time DESC")
        List<ReservationVO> findVOByUserId(Integer userId);

        /** 根据 seatId 查询该座位的预约列表（含位置信息） */
        @Select("SELECT r.reservation_id AS reservationId, r.user_id AS userId, u.username AS username, r.seat_id AS seatId, " +
                "r.start_time AS startTime, r.end_time AS endTime, r.status AS status, " +
                "s.seat_number AS seatNumber, sr.room_name AS roomName, sr.location AS location " +
                "FROM reservation r " +
                "LEFT JOIN sys_user u ON r.user_id = u.user_id " +
                "LEFT JOIN seat s ON r.seat_id = s.seat_id " +
                "LEFT JOIN study_room sr ON s.room_id = sr.room_id " +
                "WHERE r.seat_id = #{seatId} ORDER BY r.start_time DESC")
        List<ReservationVO> findVOBySeatId(Integer seatId);
}
