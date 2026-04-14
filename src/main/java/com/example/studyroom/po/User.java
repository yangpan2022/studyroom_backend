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

    /** 密码（明文存储，毕设阶段） */
    private String password;

    /**
     * 用户状态
     * active - 正常
     * banned - 封禁
     */
    private String status;
}
