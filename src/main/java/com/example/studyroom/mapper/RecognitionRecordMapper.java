package com.example.studyroom.mapper;

import com.example.studyroom.po.RecognitionRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI 识别记录 Mapper 接口（MyBatis 注解方式）
 * 对应数据库表：detect_record
 */
@Mapper
public interface RecognitionRecordMapper {

    @Select("SELECT * FROM detect_record ORDER BY detect_time DESC")
    List<RecognitionRecord> findAll();

    /** 根据座位 ID 查询识别历史（按时间倒序） */
    @Select("SELECT * FROM detect_record WHERE seat_id = #{seatId} ORDER BY detect_time DESC")
    List<RecognitionRecord> findBySeatId(Integer seatId);

    /** 
     * 查询某个 seatId 的最近 N 条记录 
     * 避免使用 detect_time，因为客户端时间不可靠，改为直接拉取落库的最近数据
     */
    @Select("SELECT * FROM detect_record WHERE seat_id = #{seatId} ORDER BY result_id DESC LIMIT #{count}")
    List<RecognitionRecord> findRecentRecordsByCount(@Param("seatId") Integer seatId, @Param("count") int count);

    @Insert("INSERT INTO detect_record(seat_id, occupied, confidence, detect_time) " +
            "VALUES(#{seatId}, #{occupied}, #{confidence}, #{detectTime})")
    @Options(useGeneratedKeys = true, keyProperty = "resultId")
    int insert(RecognitionRecord record);
}
