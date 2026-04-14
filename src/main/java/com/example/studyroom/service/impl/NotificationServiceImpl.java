package com.example.studyroom.service.impl;

import com.example.studyroom.constant.SystemConstants;
import com.example.studyroom.mapper.NotificationMapper;
import com.example.studyroom.po.Notification;
import com.example.studyroom.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知业务实现类
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public List<Notification> getByUserId(Integer userId) {
        return notificationMapper.findByUserId(userId);
    }

    @Override
    public Notification getById(Integer notificationId) {
        Notification notification = notificationMapper.findById(notificationId);
        if (notification == null) {
            throw new RuntimeException("通知不存在，ID: " + notificationId);
        }
        return notification;
    }

    @Override
    public Notification markAsRead(Integer notificationId) {
        Notification notification = getById(notificationId);
        notificationMapper.updateStatus(notificationId, SystemConstants.NotificationStatus.READ);
        notification.setStatus(SystemConstants.NotificationStatus.READ);
        return notification;
    }

    @Override
    public void deleteById(Integer notificationId) {
        getById(notificationId); // 校验存在
        notificationMapper.deleteById(notificationId);
    }

    /**
     * 发送通知（内部调用，由业务层在关键节点调用）
     * send_time 自动设为当前时间，status 默认 unread
     */
    @Override
    public void send(Integer userId, String type, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setSendTime(LocalDateTime.now());
        notification.setStatus(SystemConstants.NotificationStatus.UNREAD);
        notificationMapper.insert(notification);
    }
}
