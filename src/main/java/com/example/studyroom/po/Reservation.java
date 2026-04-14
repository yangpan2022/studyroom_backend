package com.example.studyroom.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预约实体类，对应数据库表 reservation
 */
@Data
public class Reservation {

    /** 预约ID，主键，自增 */
    private Integer reservationId;

    /** 预约用户ID */
    private Integer userId;

    /** 预约座位ID */
    private Integer seatId;

    /** 预约开始时间 */
    private LocalDateTime startTime;

    /** 预约结束时间 */
    private LocalDateTime endTime;

    /**
     * 预约状态
     * RESERVED,
     * CANCELLED,
     * COMPLETED
     */
    private String status;
}
