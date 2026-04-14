package com.example.studyroom.controller;

import com.example.studyroom.po.Notification;
import com.example.studyroom.service.NotificationService;
import com.example.studyroom.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知管理 Controller
 * 基础路径：/notifications
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 查询用户的所有通知（按时间倒序）
     * GET /notifications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public Result<List<Notification>> getByUserId(@PathVariable("userId") Integer userId) {
        return Result.success(notificationService.getByUserId(userId));
    }

    /**
     * 查询单条通知详情
     * GET /notifications/{id}
     */
    @GetMapping("/{id}")
    public Result<Notification> getById(@PathVariable("id") Integer id) {
        return Result.success(notificationService.getById(id));
    }

    /**
     * 标记通知为已读
     * PUT /notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    public Result<Notification> markAsRead(@PathVariable("id") Integer id) {
        return Result.success(notificationService.markAsRead(id));
    }

    /**
     * 删除通知
     * DELETE /notifications/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable("id") Integer id) {
        notificationService.deleteById(id);
        return Result.success();
    }
}
