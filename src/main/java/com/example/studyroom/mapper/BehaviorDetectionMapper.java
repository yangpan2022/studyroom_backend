package com.example.studyroom.mapper;

import com.example.studyroom.po.BehaviorDetection;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 行为检测 Mapper 接口
 * 对应数据库表：behavior_detection
 */
@Mapper
public interface BehaviorDetectionMapper {

    /** 插入识别记录 */
    @Insert("INSERT INTO behavior_detection(seat_id, detect_time, behavior_type, confidence) " +
            "VALUES(#{seatId}, #{detectTime}, #{behaviorType}, #{confidence})")
    @Options(useGeneratedKeys = true, keyProperty = "behaviorId")
    int insert(BehaviorDetection detection);

    /** 根据座位 ID 查询行为记录（按时间倒序） */
    @Select("SELECT * FROM behavior_detection WHERE seat_id = #{seatId} ORDER BY detect_time DESC")
    List<BehaviorDetection> findBySeatId(Integer seatId);
}
