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
    @Insert("INSERT INTO sys_user(username, password, status) VALUES(#{username}, #{password}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);

    /**
     * 更新用户基本信息（不包含密码，防止误抹除）
     */
    @Update("UPDATE sys_user SET username=#{username}, status=#{status} WHERE user_id=#{userId}")
    int update(User user);

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
}
