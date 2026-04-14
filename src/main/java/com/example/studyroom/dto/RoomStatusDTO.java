package com.example.studyroom.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 自习室当前座位状态统计
 */
@Data
public class RoomStatusDTO {

    /** 座位总数 */
    private int total;

    /** 可用数 */
    private int available;

    /** 已预约未签到 */
    private int reserved;

    /** 正在使用 */
    private int occupied;

    /** 使用率 = (reserved + occupied) / total */
    private BigDecimal usageRate;
}
