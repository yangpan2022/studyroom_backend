package com.example.studyroom.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预约展示 VO
 * 在 Reservation 实体基础上，通过 JOIN 查询额外携带：
 * - seatNumber（座位号）
 * - roomName（自习室名称）
 * - location（自习室位置）
 */
@Data
public class ReservationVO {

    private Integer reservationId;
    private Integer userId;
    
    /** 用户名（真实姓名或昵称，来自 sys_user 表） */
    private String username;

    private Integer seatId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    /** 座位号（来自 seat 表） */
    private String seatNumber;

    /** 自习室名称（来自 study_room 表） */
    private String roomName;

    /** 自习室位置（来自 study_room 表） */
    private String location;
}
