package com.example.studyroom.service;

import com.example.studyroom.po.User;

import java.util.List;

/**
 * 用户业务接口
 */
public interface UserService {

    /** 获取所有用户 */
    List<User> getAllUsers();

    /** 根据 ID 获取用户 */
    User getUserById(Integer userId);

    /** 用户登录（用户名密码验证） */
    User login(String username, String password);

    /** 新增用户 */
    User createUser(User user);

    /** 更新用户 */
    User updateUser(Integer userId, User user);

    /** 修改密码 */
    void changePassword(Integer userId, String oldPassword, String newPassword);

    /** 管理员重置密码 */
    void adminResetPassword(Integer userId, String newPassword);

    /** 删除用户 */
    void deleteUser(Integer userId);

    /** 分页查询用户 */
    java.util.Map<String, Object> getUsersPage(Integer page, Integer pageSize, String role, String keyword, String status);

    /** 管理员建号 */
    User adminCreateUser(User user);

    /** 管理员编辑用户信息 */
    User adminUpdateUser(Integer userId, User user);

    /** 更新用户状态 */
    void updateUserStatus(Integer userId, String status);

    /** 获取用户统计数据 */
    java.util.Map<String, Object> getUserStats(Integer userId);
}
