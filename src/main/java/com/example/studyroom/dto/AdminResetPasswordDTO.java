package com.example.studyroom.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理员重置密码 DTO
 */
@Data
public class AdminResetPasswordDTO {

    /** 新密码 */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
