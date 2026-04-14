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

    @Insert("INSERT INTO detect_record(seat_id, occupied, confidence, detect_time) " +
            "VALUES(#{seatId}, #{occupied}, #{confidence}, #{detectTime})")
    @Options(useGeneratedKeys = true, keyProperty = "resultId")
    int insert(RecognitionRecord record);
}
