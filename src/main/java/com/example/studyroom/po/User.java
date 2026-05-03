package com.example.studyroom.po;

import lombok.Data;

/**
 * 用户实体类，对应数据库表 sys_user
 */
@Data
public class User {

    /** 用户ID，主键，自增 */
    private Integer userId;

    /** 用户名，唯一 */
    @jakarta.validation.constraints.NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码  */
    private String password;

    /**
     * 用户角色
     * student - 学生
     * admin   - 管理员
     */
    private String role;

    /** 头像 URL（可选） */
    private String avatar;

    /** 联系方式，手机号 */
    private String contact;

    /**
     * 用户状态
     * active - 正常
     * banned - 封禁
     */
    private String status;
}
