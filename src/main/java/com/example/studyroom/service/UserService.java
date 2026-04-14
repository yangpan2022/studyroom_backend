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

    /** 删除用户 */
    void deleteUser(Integer userId);
}
