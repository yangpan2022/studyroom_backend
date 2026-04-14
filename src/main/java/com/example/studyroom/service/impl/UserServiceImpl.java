package com.example.studyroom.service.impl;

import com.example.studyroom.constant.SystemConstants;
import com.example.studyroom.mapper.UserMapper;
import com.example.studyroom.po.User;
import com.example.studyroom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户业务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    @Override
    public User getUserById(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在，ID: " + userId);
        }
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }
        if (SystemConstants.UserStatus.BANNED.equals(user.getStatus())) {
            throw new RuntimeException("该账号已被封禁");
        }
        // 返回用户信息（不返回密码字段）
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional
    public User createUser(User user) {
        // 检查用户名是否已存在
        User existing = userMapper.findByUsername(user.getUsername());
        if (existing != null) {
            throw new RuntimeException("用户名已存在：" + user.getUsername());
        }
        // 默认状态为 active
        if (user.getStatus() == null || user.getStatus().isBlank()) {
            user.setStatus(SystemConstants.UserStatus.ACTIVE);
        }
        userMapper.insert(user);
        // 清空密码后返回
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional
    public User updateUser(Integer userId, User user) {
        // 确认用户存在
        getUserById(userId);
        user.setUserId(userId);
        // Mapper.update 现在不包含 password 字段，安全
        userMapper.update(user);
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        getUserById(userId); // 确认用户存在，不存在则抛异常
        userMapper.deleteById(userId);
    }
}
