package com.example.studyroom.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 按小时统计占用率
 */
@Data
public class HourlyUsageDTO {

    /** 小时（0-23） */
    private int hour;

    /** 占用率 */
    private BigDecimal usageRate;

    public HourlyUsageDTO(int hour, BigDecimal usageRate) {
        this.hour = hour;
        this.usageRate = usageRate;
    }
}
