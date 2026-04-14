package com.example.studyroom.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 行为检测实体类
 * 对应数据库表：behavior_detection
 */
@Data
public class BehaviorDetection {

    /** 行为检测ID，主键，自增 */
    private Integer behaviorId;

    /** 座位ID */
    private Integer seatId;

    /** 检测时间 */
    private LocalDateTime detectTime;

    /** 行为类型 (例如: phone_detected) */
    private String behaviorType;

    /** 置信度 (0.00 ~ 1.00) */
    private BigDecimal confidence;
}
