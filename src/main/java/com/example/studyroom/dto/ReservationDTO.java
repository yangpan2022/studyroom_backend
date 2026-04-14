package com.example.studyroom.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建预约请求对象
 */
@Data
public class ReservationDTO {

    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @NotNull(message = "座位ID不能为空")
    private Integer seatId;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
}
