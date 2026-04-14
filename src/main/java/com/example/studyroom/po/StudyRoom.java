package com.example.studyroom.po;

import lombok.Data;

/**
 * 自习室实体类，对应数据库表 study_room
 */
@Data
public class StudyRoom {

    /** 自习室ID，主键，自增 */
    private Integer roomId;

    /** 自习室名称 */
    private String roomName;

    /** 座位容量 */
    private Integer capacity;

    /**
     * 自习室状态
     * open - 开放
     * closed - 关闭
     */
    private String status;
}
