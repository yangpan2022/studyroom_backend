package com.example.studyroom.service;

import com.example.studyroom.po.Notification;

import java.util.List;

/**
 * 通知业务接口
 */
public interface NotificationService {

    /** 查询用户所有通知 */
    List<Notification> getByUserId(Integer userId);

    /** 查询单条通知 */
    Notification getById(Integer notificationId);

    /** 标记通知已读 */
    Notification markAsRead(Integer notificationId);

    /** 删除通知 */
    void deleteById(Integer notificationId);

    /**
     * 发送一条通知（内部调用）
     *
     * @param userId  目标用户
     * @param type    通知类型（SystemConstants.NotificationType）
     * @param message 通知文本内容
     */
    void send(Integer userId, String type, String message);
}
