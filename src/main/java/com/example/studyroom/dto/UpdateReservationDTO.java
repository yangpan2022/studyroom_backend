package com.example.studyroom.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 修改预约请求对象
 */
@Data
public class UpdateReservationDTO {

    @NotNull(message = "座位ID不能为空")
    private Integer seatId;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
}
