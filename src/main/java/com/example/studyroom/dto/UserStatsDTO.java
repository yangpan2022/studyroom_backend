package com.example.studyroom.dto;

import lombok.Data;

/**
 * 用户个人使用情况统计
 */
@Data
public class UserStatsDTO {

    /** 预约总数 */
    private int reservationCount;

    /** 签到总数 */
    private int checkInCount;

    /** 已完成数 */
    private int completedCount;

    /** 已取消数 */
    private int cancelledCount;

    /** 被自动释放次数 */
    private int autoReleaseCount;

    /** 手机警告次数 */
    private int phoneWarningCount;
}
