package com.example.studyroom.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 识别记录实体类
 * 对应数据库表：detect_record
 *
 * 表字段：
 * result_id - 主键自增
 * seat_id - 被识别的座位 ID
 * detect_time - 识别时间
 * occupied - 是否检测到人员 (tinyint(1))
 * confidence - 置信度 (decimal 5,2)
 */
@Data
public class RecognitionRecord {

    /** 记录ID，主键，自增 */
    private Integer resultId;

    /** 被识别的座位ID */
    private Integer seatId;

    /** 是否检测到人员占用 */
    private Boolean occupied;

    /** 识别置信度（0.0 ~ 1.0） */
    private Double confidence;

    /** 识别时间 */
    private LocalDateTime detectTime;
}
