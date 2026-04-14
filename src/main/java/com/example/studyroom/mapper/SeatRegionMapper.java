package com.example.studyroom.mapper;

import com.example.studyroom.po.SeatRegion;
import org.apache.ibatis.annotations.*;

/**
 * 座位区域 Mapper 接口（MyBatis 注解方式）
 */
@Mapper
public interface SeatRegionMapper {

    /** 根据座位 ID 查询区域标定信息 */
    @Select("SELECT * FROM seat_region WHERE seat_id = #{seatId}")
    SeatRegion findBySeatId(Integer seatId);

    @Insert("INSERT INTO seat_region(seat_id, x1, y1, x2, y2) VALUES(#{seatId}, #{x1}, #{y1}, #{x2}, #{y2})")
    @Options(useGeneratedKeys = true, keyProperty = "regionId")
    int insert(SeatRegion region);

    @Update("UPDATE seat_region SET x1=#{x1}, y1=#{y1}, x2=#{x2}, y2=#{y2} WHERE seat_id=#{seatId}")
    int updateBySeatId(SeatRegion region);

    @Delete("DELETE FROM seat_region WHERE seat_id = #{seatId}")
    int deleteBySeatId(Integer seatId);
}
