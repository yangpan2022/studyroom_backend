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
        public static final String RESERVED = "reserved"; // 预约成功，等待使用
        public static final String COMPLETED = "completed"; // 正常完成
        public static final String CANCELLED = "cancelled"; // 已取消
    }

    /** 通知状态 */
    public static final class NotificationStatus {
        public static final String UNREAD = "unread";
        public static final String READ = "read";
    }

    /** 通知类型 */
    public static final class NotificationType {
        public static final String RESERVATION_SUCCESS = "reservation_success";
        public static final String RESERVATION_CANCELLED = "reservation_cancelled";
        public static final String CHECKIN_SUCCESS = "checkin_success";
        public static final String RESERVATION_COMPLETED = "reservation_completed";
        public static final String SEAT_AUTO_RELEASED = "seat_auto_released";
        public static final String SYSTEM_NOTICE = "system_notice";
        public static final String STUDY_WARNING = "study_warning";
        public static final String SEAT_CONFLICT_WARNING = "seat_conflict_warning";
    }

    /** 行为检测类型 */
    public static final class BehaviorType {
        public static final String PHONE_DETECTED = "phone_detected";
    }
}
