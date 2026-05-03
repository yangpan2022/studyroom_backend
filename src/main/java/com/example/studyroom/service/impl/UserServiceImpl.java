package com.example.studyroom.service.impl;

import com.example.studyroom.constant.SystemConstants;
import com.example.studyroom.mapper.NotificationMapper;
import com.example.studyroom.mapper.ReservationMapper;
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

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private NotificationMapper notificationMapper;

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
        // 默认 role = student，不允许前端自行指定，防止越权
        user.setRole(SystemConstants.UserRole.STUDENT);
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
        // Mapper.update 现在包含 password 字段
        userMapper.update(user);
        user.setPassword(null);
        return user;
    }

    /**
     * 校验密码复杂度：至少 8 位且包含至少两类字符
     */
    private void validatePasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("密码至少8位，且需包含数字、字母大小写或符号中的至少两类");
        }
        int types = 0;
        if (password.matches(".*[0-9].*")) types++;
        if (password.matches(".*[a-z].*")) types++;
        if (password.matches(".*[A-Z].*")) types++;
        if (password.matches(".*[^a-zA-Z0-9].*")) types++;

        if (types < 2) {
            throw new RuntimeException("密码至少8位，且需包含数字、字母大小写或符号中的至少两类");
        }
    }

    @Override
    @Transactional
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = getUserById(userId); // 确认用户存在
        
        // 校验旧密码
        if (!user.getPassword().equals(oldPassword)) {
            throw new RuntimeException("原密码错误");
        }
        
        // 校验新密码
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("新密码不能为空");
        }
        validatePasswordComplexity(newPassword);
        
        // 更新密码
        userMapper.updatePassword(userId, newPassword);
    }

    @Override
    @Transactional
    public void adminResetPassword(Integer userId, String newPassword) {
        getUserById(userId); // 确认用户存在，不存在则抛异常
        
        // 校验新密码
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("新密码不能为空");
        }
        validatePasswordComplexity(newPassword);
        
        // 更新密码
        userMapper.updatePassword(userId, newPassword);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        User user = getUserById(userId); // 确认用户存在，不存在则抛异常
        
        // 不能删除最后一个 admin
        if (SystemConstants.UserRole.ADMIN.equals(user.getRole())) {
            if (userMapper.countActiveAdmins() <= 1) {
                throw new RuntimeException("不能删除系统中最后一个活跃的管理员");
            }
        }
        
        // 如果用户存在历史预约记录，则禁止物理删除
        if (reservationMapper.countByUserId(userId) > 0) {
             throw new RuntimeException("该用户存在历史预约记录，无法删除，请使用禁用账号功能");
        }
        
        userMapper.deleteById(userId);
    }

    @Override
    public java.util.Map<String, Object> getUsersPage(Integer page, Integer pageSize, String role, String keyword, String status) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (page - 1) * pageSize;

        List<User> records = userMapper.findUsersPage(role, keyword, status, offset, pageSize);
        // 不返回密码
        for (User u : records) {
            u.setPassword(null);
        }
        long total = userMapper.countUsers(role, keyword, status);

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("records", records);
        result.put("total", total);
        return result;
    }

    @Override
    @Transactional
    public User adminCreateUser(User user) {
        // 检查用户名是否已存在
        User existing = userMapper.findByUsername(user.getUsername());
        if (existing != null) {
            throw new RuntimeException("用户名已存在：" + user.getUsername());
        }
        
        if (user.getRole() == null || (!SystemConstants.UserRole.ADMIN.equals(user.getRole()) && !SystemConstants.UserRole.STUDENT.equals(user.getRole()))) {
             throw new RuntimeException("非法的用户角色");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword("123456");
        }
        
        if (user.getStatus() == null || user.getStatus().isBlank()) {
            user.setStatus(SystemConstants.UserStatus.ACTIVE);
        }
        
        userMapper.insert(user);
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional
    public User adminUpdateUser(Integer userId, User user) {
        User existing = getUserById(userId);

        // 检查 username 是否与其他用户冲突
        User byUsername = userMapper.findByUsername(user.getUsername());
        if (byUsername != null && !byUsername.getUserId().equals(userId)) {
            throw new RuntimeException("用户名已存在：" + user.getUsername());
        }

        if (!SystemConstants.UserRole.ADMIN.equals(user.getRole()) && !SystemConstants.UserRole.STUDENT.equals(user.getRole())) {
            throw new RuntimeException("非法的用户角色");
        }

        if (!SystemConstants.UserStatus.ACTIVE.equals(user.getStatus()) && !SystemConstants.UserStatus.BANNED.equals(user.getStatus())) {
            throw new RuntimeException("非法的用户状态");
        }

        // 保护：如果当前是一个活跃的 admin，即将变成只读或者降低权限，需要确保系统内还有别的 active admin
        if (SystemConstants.UserRole.ADMIN.equals(existing.getRole()) && SystemConstants.UserStatus.ACTIVE.equals(existing.getStatus())) {
            boolean isDemoting = !SystemConstants.UserRole.ADMIN.equals(user.getRole());
            boolean isBanning = !SystemConstants.UserStatus.ACTIVE.equals(user.getStatus());

            if (isDemoting || isBanning) {
                if (userMapper.countActiveAdmins() <= 1) {
                    throw new RuntimeException("不能禁用或降级系统中最后一个活跃的管理员");
                }
            }
        }

        user.setUserId(userId);
        userMapper.updateByAdmin(user);
        
        // 查询最新并清空密码返回
        User updated = userMapper.findById(userId);
        updated.setPassword(null);
        return updated;
    }

    @Override
    @Transactional
    public void updateUserStatus(Integer userId, String status) {
        if (!SystemConstants.UserStatus.ACTIVE.equals(status) && !SystemConstants.UserStatus.BANNED.equals(status)) {
            throw new RuntimeException("非法的用户状态: " + status);
        }
        
        User user = getUserById(userId); // confirm exists
        
        if (SystemConstants.UserStatus.BANNED.equals(status) && SystemConstants.UserRole.ADMIN.equals(user.getRole())) {
             if (userMapper.countActiveAdmins() <= 1 && SystemConstants.UserStatus.ACTIVE.equals(user.getStatus())) {
                  throw new RuntimeException("不能禁用系统中最后一个活跃的管理员");
             }
        }
        // TODO: 如果当前没有完整登录态判断“是否禁用自己”，先保留 TODO
        
        userMapper.updateStatus(userId, status);
    }

    @Override
    public java.util.Map<String, Object> getUserStats(Integer userId) {
        getUserById(userId); // ensure user exists

        int totalReservations = reservationMapper.countByUserId(userId);
        int completedReservations = reservationMapper.countByUserIdAndStatus(userId, SystemConstants.ReservationStatus.COMPLETED);
        int cancelledReservations = reservationMapper.countByUserIdAndStatus(userId, SystemConstants.ReservationStatus.CANCELLED);
        
        Integer studyMinutesObj = reservationMapper.sumCompletedStudyMinutes(userId);
        int totalStudyMinutes = studyMinutesObj == null ? 0 : studyMinutesObj;
        double totalStudyHours = Math.round((totalStudyMinutes / 60.0) * 10.0) / 10.0;
        
        int phoneWarningCount = notificationMapper.countByUserIdAndType(userId, SystemConstants.NotificationType.STUDY_WARNING);
        int autoReleaseCount = notificationMapper.countByUserIdAndType(userId, SystemConstants.NotificationType.SEAT_AUTO_RELEASED);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalReservations", totalReservations);
        stats.put("completedReservations", completedReservations);
        stats.put("cancelledReservations", cancelledReservations);
        stats.put("totalStudyMinutes", totalStudyMinutes);
        stats.put("totalStudyHours", totalStudyHours);
        stats.put("phoneWarningCount", phoneWarningCount);
        stats.put("autoReleaseCount", autoReleaseCount);

        return stats;
    }
}
