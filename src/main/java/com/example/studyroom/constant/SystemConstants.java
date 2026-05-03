package com.example.studyroom.constant;

/**
 * 系统常量类，统一管理业务状态字符串
 */
public class SystemConstants {

    /** 用户状态 */
    public static final class UserStatus {
        public static final String ACTIVE = "active";
        public static final String BANNED = "banned";
    }

    /** 用户角色 */
    public static final class UserRole {
        public static final String STUDENT = "student";
        public static final String ADMIN   = "admin";
    }

    /** 自习室状态 */
    public static final class RoomStatus {
        public static final String OPEN = "open";
        public static final String CLOSED = "closed";
    }

    /** 座位状态 */
    public static final class SeatStatus {
        public static final String AVAILABLE = "available";
        public static final String OCCUPIED = "occupied"; // 正在使用
        public static final String RESERVED = "reserved"; // 已预约未签到
        public static final String DISABLED = "disabled";
    }

    /** 预约状态（需与数据库枚举一致） */
    public static final class ReservationStatus {
        public static final String RESERVED  = "reserved";  // 已预约，等待签到
        public static final String OCCUPIED  = "occupied";  // 已签到，使用中
        public static final String COMPLETED = "completed"; // 已签退/完成
        public static final String CANCELLED = "cancelled"; // 已取消
    }

    /** 通知状态 */
    public static final class NotificationStatus {
        public static final String UNREAD = "unread";
        public static final String READ = "read";
    }

    /** 通知类型 */
    public static final class NotificationType {
        public static final String RESERVATION_SUCCESS       = "reservation_success";
        public static final String RESERVATION_CANCELLED     = "reservation_cancelled";
        public static final String CHECKIN_SUCCESS           = "checkin_success";
        public static final String RESERVATION_COMPLETED     = "reservation_completed";
        public static final String SEAT_AUTO_RELEASED        = "seat_auto_released";
        public static final String SYSTEM_NOTICE             = "system_notice";
        public static final String STUDY_WARNING             = "study_warning";
        public static final String SEAT_CONFLICT_WARNING     = "seat_conflict_warning";
        /** 预约即将开始提醒（提前 15 分钟） */
        public static final String RESERVATION_REMINDER_START = "reservation_reminder_start";
        /** 预约即将结束提醒（提前 15 分钟） */
        public static final String RESERVATION_REMINDER_END   = "reservation_reminder_end";
        /** 系统自动签退（预约结束时间已过，status=occupied 自动完成） */
        public static final String RESERVATION_COMPLETED_AUTO = "reservation_completed_auto";
    }

    /** 行为检测类型 */
    public static final class BehaviorType {
        public static final String PHONE_DETECTED = "phone_detected";
    }
}
