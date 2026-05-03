package com.example.studyroom.po;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 座位区域实体类，对应数据库表 seat_region
 * 用于标定摄像头画面中每个座位对应的相对区域坐标（0~1 小数）
 */
@Data
public class SeatRegion {

    /** 区域ID，主键，自增 */
    private Integer regionId;

    /** 关联的座位ID */
    private Integer seatId;

    /** 矩形框左上角 x 相对坐标（0~1） */
    private BigDecimal x1;

    /** 矩形框左上角 y 相对坐标（0~1） */
    private BigDecimal y1;

    /** 矩形框右下角 x 相对坐标（0~1） */
    private BigDecimal x2;

    /** 矩形框右下角 y 相对坐标（0~1） */
    private BigDecimal y2;
}
