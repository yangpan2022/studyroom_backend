package com.example.studyroom.controller;

import com.example.studyroom.dto.LoginDTO;
import com.example.studyroom.po.User;
import com.example.studyroom.service.UserService;
import com.example.studyroom.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理 Controller
 * 基础路径：/users
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户列表
     * GET /users
     */
    @GetMapping
    public Result<List<User>> getAllUsers() {
        return Result.success(userService.getAllUsers());
    }

    /**
     * 根据 ID 获取用户
     * GET /users/{id}
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") Integer id) {
        return Result.success(userService.getUserById(id));
    }

    /**
     * 用户登录
     * POST /users/login
     * Body: { "username": "xxx", "password": "xxx" }
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody @Valid LoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO.getUsername(), loginDTO.getPassword()));
    }

    /**
     * 新增用户（注册）
     * POST /users
     * Body: { "username": "xxx", "password": "xxx", "status": "active" }
     */
    @PostMapping
    public Result<User> createUser(@RequestBody @Valid User user) {
        return Result.success(userService.createUser(user));
    }

    /**
     * 更新用户信息
     * PUT /users/{id}
     */
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable("id") Integer id, @RequestBody @Valid User user) {
        return Result.success(userService.updateUser(id, user));
    }

    /**
     * 删除用户
     * DELETE /users/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
