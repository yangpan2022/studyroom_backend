package com.example.studyroom.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知实体类，对应数据库表 notification
 */
@Data
public class Notification {

    /** 通知ID，主键，自增 */
    private Integer notificationId;

    /** 接收通知的用户ID */
    private Integer userId;

    /** 通知内容 */
    private String message;

    /** 发送时间 */
    private LocalDateTime sendTime;

    /**
     * 通知类型
     * reservation_success / reservation_cancelled /
     * checkin_success / reservation_completed / seat_auto_released / system_notice
     */
    private String type;

    /**
     * 通知状态
     * unread / read
     */
    private String status;
}
