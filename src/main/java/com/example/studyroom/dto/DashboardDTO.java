package com.example.studyroom.dto;

import lombok.Data;

/**
 * 系统总览 Dashboard 数据
 */
@Data
public class DashboardDTO {

    /** 用户总数 */
    private int userCount;

    /** 自习室总数 */
    private int roomCount;

    /** 座位总数 */
    private int seatCount;

    /** 当前占用座位数 */
    private int occupiedCount;

    /** 今日预约数 */
    private int todayReservationCount;

    /** 今日签到数 */
    private int todayCheckInCount;

    /** 今日 AI 自动释放数 */
    private int todayAutoReleaseCount;

    /** 今日手机提醒数 */
    private int todayPhoneWarningCount;
}
