package com.example.studyroom.mapper;

import com.example.studyroom.po.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户 Mapper 接口（MyBatis 注解方式）
 */
@Mapper
public interface UserMapper {

    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM sys_user")
    List<User> findAll();

    /**
     * 根据 ID 查询用户
     */
    @Select("SELECT * FROM sys_user WHERE user_id = #{userId}")
    User findById(Integer userId);

    /**
     * 根据用户名查询用户（用于登录验证）
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 新增用户
     * useGeneratedKeys=true 表示使用数据库自增主键，keyProperty 将主键回填到对象
     */
    @Insert("INSERT INTO sys_user(username, password, role, status) VALUES(#{username}, #{password}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);

    /**
     * 更新用户基本信息（不包含密码，防止误抹除）
     */
    @Update("UPDATE sys_user SET username=#{username}, avatar=#{avatar}, contact=#{contact}, status=#{status} WHERE user_id=#{userId}")
    int update(User user);

    /**
     * 管理员专用更新接口：允许修改角色的同时不碰密码和头像
     */
    @Update("UPDATE sys_user SET username=#{username}, role=#{role}, contact=#{contact}, status=#{status} WHERE user_id=#{userId}")
    int updateByAdmin(User user);

    /**
     * 单独更新密码接口
     */
    @Update("UPDATE sys_user SET password=#{password} WHERE user_id=#{userId}")
    int updatePassword(@Param("userId") Integer userId, @Param("password") String password);

    /**
     * 删除用户
     */
    @Delete("DELETE FROM sys_user WHERE user_id = #{userId}")
    int deleteById(Integer userId);

    @Select("<script>" +
            "SELECT * FROM sys_user " +
            "<where> " +
            "<if test='role != null and role != \"\"'> AND role = #{role} </if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "<if test='keyword != null and keyword != \"\"'> AND (username LIKE CONCAT('%', #{keyword}, '%') OR contact LIKE CONCAT('%', #{keyword}, '%')) </if>" +
            "</where> " +
            "ORDER BY user_id DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<User> findUsersPage(
            @Param("role") String role,
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    @Select("<script>" +
            "SELECT COUNT(1) FROM sys_user " +
            "<where> " +
            "<if test='role != null and role != \"\"'> AND role = #{role} </if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "<if test='keyword != null and keyword != \"\"'> AND (username LIKE CONCAT('%', #{keyword}, '%') OR contact LIKE CONCAT('%', #{keyword}, '%')) </if>" +
            "</where>" +
            "</script>")
    long countUsers(
            @Param("role") String role,
            @Param("keyword") String keyword,
            @Param("status") String status);

    @Update("UPDATE sys_user SET status=#{status} WHERE user_id=#{userId}")
    int updateStatus(@Param("userId") Integer userId, @Param("status") String status);

    @Select("SELECT COUNT(1) FROM sys_user WHERE role = 'admin' AND status = 'active'")
    int countActiveAdmins();
}
