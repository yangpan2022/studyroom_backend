package com.example.studyroom.mapper;

import com.example.studyroom.po.Seat;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 座位 Mapper 接口（MyBatis 注解方式）
 */
@Mapper
public interface SeatMapper {

    @Select("SELECT * FROM seat")
    List<Seat> findAll();

    @Select("SELECT * FROM seat WHERE seat_id = #{seatId}")
    Seat findById(Integer seatId);

    /** 根据自习室 ID 查询所有座位 */
    @Select("SELECT * FROM seat WHERE room_id = #{roomId}")
    List<Seat> findByRoomId(Integer roomId);

    @Insert("INSERT INTO seat(room_id, seat_number, status) VALUES(#{roomId}, #{seatNumber}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "seatId")
    int insert(Seat seat);

    @Update("UPDATE seat SET room_id=#{roomId}, seat_number=#{seatNumber}, status=#{status} WHERE seat_id=#{seatId}")
    int update(Seat seat);

    /** 仅更新座位状态（AI 识别模块和预约模块频繁调用） */
    @Update("UPDATE seat SET status=#{status} WHERE seat_id=#{seatId}")
    int updateStatus(@Param("seatId") Integer seatId, @Param("status") String status);

    @Delete("DELETE FROM seat WHERE seat_id = #{seatId}")
    int deleteById(Integer seatId);
}
