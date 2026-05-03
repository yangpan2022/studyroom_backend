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

    /** 位置信息 */
    private String location;

    /** 座位容量 */
    private Integer capacity;

    /**
     * 自习室状态
     * open - 开放
     * closed - 关闭
     */
    private String status;

    /**
     * 视频源地址
     * 示例："/media/videos/4_people_at_studyroom.mp4"
     * 为空时表示未配置视频源，前端应给出提示
     */
    private String videoUrl;

    /**
     * 视频源类型
     * "video"  - 本地/远程视频文件（当前使用）
     * "camera" - 摄像头实时流（预留）
     */
    private String videoType;
}
