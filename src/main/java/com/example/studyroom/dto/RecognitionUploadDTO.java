package com.example.studyroom.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 识别结果上传 DTO
 */
@Data
public class RecognitionUploadDTO {

    @NotNull(message = "座位ID不能为空")
    private Integer seatId;

    @NotNull(message = "占用状态不能为空")
    private Boolean occupied;

    private Double confidence;

    @NotNull(message = "识别时间不能为空")
    private LocalDateTime detectTime;
}
