package com.example.studyroom.po;

import lombok.Data;

/**
 * 座位区域实体类，对应数据库表 seat_region
 * 用于标定摄像头画面中每个座位对应的像素区域（矩形框）
 */
@Data
public class SeatRegion {

    /** 区域ID，主键，自增 */
    private Integer regionId;

    /** 关联的座位ID */
    private Integer seatId;

    /** 矩形框左上角 x 坐标 */
    private Integer x1;

    /** 矩形框左上角 y 坐标 */
    private Integer y1;

    /** 矩形框右下角 x 坐标 */
    private Integer x2;

    /** 矩形框右下角 y 坐标 */
    private Integer y2;
}
