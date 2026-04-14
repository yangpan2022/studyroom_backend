package com.example.studyroom.mapper;

import com.example.studyroom.po.Reservation;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 预约 Mapper 接口（MyBatis 注解方式）
 */
@Mapper
public interface ReservationMapper {

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

        @Insert("INSERT INTO reservation(user_id, seat_id, start_time, end_time, status) " +
                        "VALUES(#{userId}, #{seatId}, #{startTime}, #{endTime}, #{status})")
        @Options(useGeneratedKeys = true, keyProperty = "reservationId")
        int insert(Reservation reservation);

        /** 更新预约状态 */
        @Update("UPDATE reservation SET status=#{status} WHERE reservation_id=#{reservationId}")
        int updateStatus(@Param("reservationId") Integer reservationId, @Param("status") String status);

        /**
         * 根据座位 ID 查询当前正在使用的有效预约（status = reserved）
         * 增加 NOW() 时间过滤，只返回当前时间在 [start_time, end_time] 区间内的预约
         * 用于：AI 自动释放 / 手机检测通知 精确定位用户
         */
        @Select("SELECT * FROM reservation WHERE seat_id = #{seatId} " +
                        "AND status = 'reserved' " +
                        "AND start_time <= NOW() AND end_time >= NOW() " +
                        "LIMIT 1")
        Reservation findActiveReservationBySeatId(Integer seatId);

        @Delete("DELETE FROM reservation WHERE reservation_id = #{reservationId}")
        int deleteById(Integer reservationId);
}
