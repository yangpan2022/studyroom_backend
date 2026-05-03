package com.example.studyroom.po;

import lombok.Data;

/**
 * 座位实体类，对应数据库表 seat
 */
@Data
public class Seat {

    /** 座位ID，主键，自增 */
    private Integer seatId;

    /** 所属自习室ID */
    private Integer roomId;

    /** 座位编号（如 A01、B02），同一自习室内唯一 */
    private String seatNumber;

    /**
     * 座位状态
     * available - 可预约
     * occupied - 已占用
     * reserved - 已预约（尚未签到）
     * disabled - 不可用
     */
    private String status;

    /** 是否启用摄像头（1=启用，0=关闭） */
    private Integer cameraEnabled;
}
