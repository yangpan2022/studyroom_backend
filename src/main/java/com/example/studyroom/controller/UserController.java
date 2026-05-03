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
     * 获取所有用户列表 (不推荐分页场景使用)
     * GET /users
     */
    @GetMapping
    public Result<List<User>> getAllUsers() {
        return Result.success(userService.getAllUsers());
    }

    /**
     * 分页条件查询用户列表 (专门面向 Admin 管理端)
     * GET /users/page?page=1&pageSize=10&role=student&status=active&keyword=xxx
     */
    @GetMapping("/page")
    public Result<java.util.Map<String, Object>> getUsersPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status) {
        return Result.success(userService.getUsersPage(page, pageSize, role, keyword, status));
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
     * 新增用户（自行注册）
     * POST /users
     * Body: { "username": "xxx", "password": "xxx", "contact": "phone" }
     */
    @PostMapping
    public Result<User> createUser(@RequestBody @Valid User user) {
        return Result.success(userService.createUser(user));
    }

    /**
     * 管理员建号
     * POST /users/admin_create
     * Body: { "username": "xxx", "password": "xxx", "role": "admin", "contact": "phone" }
     */
    @PostMapping("/admin_create")
    public Result<User> adminCreateUser(@RequestBody @Valid User user) {
        return Result.success(userService.adminCreateUser(user));
    }

    /**
     * 管理员编辑用户信息
     * PUT /users/{id}/admin_update
     * Body: { "username": "xxx", "role": "student", "contact": "phone", "status": "active" }
     */
    @PutMapping("/{id}/admin_update")
    public Result<User> adminUpdateUser(@PathVariable("id") Integer id, @RequestBody @Valid User user) {
        return Result.success(userService.adminUpdateUser(id, user));
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
     * 更新个人信息（头像 / 联系方式 / 用户名）
     * PUT /users/{id}/profile
     * Body: { "username": "xxx", "avatar": "url", "contact": "phone" }
     * 注意：前端不可通过此接口修改 role 或 status
     */
    @PutMapping("/{id}/profile")
    public Result<User> updateProfile(@PathVariable("id") Integer id, @RequestBody User user) {
        // 强制清空敏感字段，防止越权
        user.setRole(null);
        user.setStatus(null);
        user.setPassword(null);
        return Result.success(userService.updateUser(id, user));
    }

    /**
     * 修改密码
     * PUT /users/{id}/password
     * Body: { "oldPassword": "xxx", "newPassword": "xxx" }
     */
    @PutMapping("/{id}/password")
    public Result<Void> updatePassword(@PathVariable("id") Integer id, @RequestBody @Valid com.example.studyroom.dto.ChangePasswordDTO dto) {
        userService.changePassword(id, dto.getOldPassword(), dto.getNewPassword());
        return Result.success();
    }

    /**
     * 管理员重置密码
     * PUT /users/{id}/admin_reset_password
     * Body: { "newPassword": "123456" }
     */
    @PutMapping("/{id}/admin_reset_password")
    public Result<Void> adminResetPassword(@PathVariable("id") Integer id, @RequestBody @Valid com.example.studyroom.dto.AdminResetPasswordDTO dto) {
        userService.adminResetPassword(id, dto.getNewPassword());
        return Result.success();
    }

    /**
     * 删除用户 (安全删除防线在 Service 层)
     * DELETE /users/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 启用/禁用账号
     * PUT /users/{id}/status
     * Body: { "status": "banned" }
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable("id") Integer id, @RequestBody java.util.Map<String, String> body) {
        String status = body.get("status");
        if (status == null) {
            throw new RuntimeException("status 不能为空");
        }
        userService.updateUserStatus(id, status);
        return Result.success();
    }

    /**
     * 获取用户统计（预约次数、学习时长等）
     * GET /users/{id}/stats
     */
    @GetMapping("/{id}/stats")
    public Result<java.util.Map<String, Object>> getUserStats(@PathVariable("id") Integer id) {
        return Result.success(userService.getUserStats(id));
    }
}
