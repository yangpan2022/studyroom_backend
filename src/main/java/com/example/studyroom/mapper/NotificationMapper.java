package com.example.studyroom.mapper;

import com.example.studyroom.po.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 通知 Mapper 接口（MyBatis 注解方式）
 * 对应数据库表：notification
 */
@Mapper
public interface NotificationMapper {

        /** 根据用户 ID 查询所有通知（按发送时间倒序） */
        @Select("SELECT * FROM notification WHERE user_id = #{userId} ORDER BY send_time DESC")
        List<Notification> findByUserId(Integer userId);

        /** 根据通知 ID 查询单条 */
        @Select("SELECT * FROM notification WHERE notification_id = #{notificationId}")
        Notification findById(Integer notificationId);

        /** 插入一条通知 */
        @Insert("INSERT INTO notification(user_id, message, send_time, type, status) " +
                        "VALUES(#{userId}, #{message}, #{sendTime}, #{type}, #{status})")
        @Options(useGeneratedKeys = true, keyProperty = "notificationId")
        int insert(Notification notification);

        /** 标记为已读 */
        @Update("UPDATE notification SET status = #{status} WHERE notification_id = #{notificationId}")
        int updateStatus(@Param("notificationId") Integer notificationId, @Param("status") String status);

        /** 删除通知 */
        @Delete("DELETE FROM notification WHERE notification_id = #{notificationId}")
        int deleteById(Integer notificationId);

        /**
         * 查询用户最近 5 分钟内是否已收到 study_warning 通知
         * 用于防止 AI 高频上传导致通知轰炸
         */
        @Select("SELECT * FROM notification WHERE user_id = #{userId} " +
                        "AND type = 'study_warning' " +
                        "AND send_time >= NOW() - INTERVAL 5 MINUTE " +
                        "ORDER BY send_time DESC LIMIT 1")
        Notification findRecentWarning(Integer userId);

        /**
         * 查询用户最近 5 分钟内是否已收到 seat_conflict_warning 通知
         * 仅依赖 userId + type + 时间窗口，不解析 message 文本，安全可靠
         */
        @Select("SELECT * FROM notification WHERE user_id = #{userId} " +
                        "AND type = 'seat_conflict_warning' " +
                        "AND send_time >= NOW() - INTERVAL 5 MINUTE " +
                        "ORDER BY send_time DESC LIMIT 1")
        Notification findRecentConflictWarning(Integer userId);

        /**
         * 查询指定 seatNumber 是否已发过即将开始提醒
         */
        @Select("SELECT * FROM notification " +
                        "WHERE type = 'reservation_reminder_start' " +
                        "AND message LIKE CONCAT('%', #{seatNumber}, ' 号座位即将开始%') " +
                        "AND send_time >= DATE_SUB(NOW(), INTERVAL 30 MINUTE) " +
                        "LIMIT 1")
        Notification findRecentReminderStart(@Param("seatNumber") String seatNumber);

        /**
         * 查询指定 seatNumber 是否已发过即将结束提醒
         */
        @Select("SELECT * FROM notification " +
                        "WHERE type = 'reservation_reminder_end' " +
                        "AND message LIKE CONCAT('%', #{seatNumber}, ' 号座位即将结束%') " +
                        "AND send_time >= DATE_SUB(NOW(), INTERVAL 30 MINUTE) " +
                        "LIMIT 1")
        Notification findRecentReminderEnd(@Param("seatNumber") String seatNumber);

        @Select("SELECT COUNT(1) FROM notification WHERE user_id = #{userId} AND type = #{type}")
        int countByUserIdAndType(@Param("userId") Integer userId, @Param("type") String type);
}
