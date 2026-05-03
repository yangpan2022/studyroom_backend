package com.example.studyroom.scheduler;

import com.example.studyroom.constant.SystemConstants;
import com.example.studyroom.mapper.NotificationMapper;
import com.example.studyroom.mapper.ReservationMapper;
import com.example.studyroom.mapper.SeatMapper;
import com.example.studyroom.po.Reservation;
import com.example.studyroom.po.Seat;
import com.example.studyroom.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约提醒定时任务
 *
 * 逻辑：
 * 1. 每 60 秒执行一次
 * 2. 预约即将开始：reservation.start_time 在未来 1~15 分钟内，status=reserved
 *    → 发送 reservation_reminder_start，每个预约仅发一次（notification 表去重）
 * 3. 预约即将结束：reservation.end_time 在未来 1~15 分钟内，status=occupied
 *    → 发送 reservation_reminder_end，每个预约仅发一次
 */
@Slf4j
@Component
public class ReservationReminderScheduler {

    /** 提前通知窗口（分钟）：进入此窗口后发提醒 */
    private static final long REMIND_AHEAD_MINUTES = 15L;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private SeatMapper seatMapper;

    /**
     * 预约即将开始提醒
     * 每 60 秒扫描一次：start_time 在 [now+1min, now+15min] 内 且 status=reserved
     */
    @Scheduled(fixedDelay = 60_000)
    public void remindUpcomingStart() {
        LocalDateTime now   = LocalDateTime.now();
        LocalDateTime from  = now.plusMinutes(1);
        LocalDateTime until = now.plusMinutes(REMIND_AHEAD_MINUTES);

        List<Reservation> upcoming = reservationMapper.findByStatusAndStartTimeBetween(
                SystemConstants.ReservationStatus.RESERVED, from, until);

        for (Reservation r : upcoming) {
            Seat seat = seatMapper.findById(r.getSeatId());
            if (seat == null) continue;
            String seatNumber = seat.getSeatNumber();

            // 去重：已发过此预约的开始提醒则跳过
            if (notificationMapper.findRecentReminderStart(seatNumber) != null) {
                continue;
            }
            notificationService.send(
                    r.getUserId(),
                    SystemConstants.NotificationType.RESERVATION_REMINDER_START,
                    "您预约的 " + seatNumber + " 号座位即将开始，请按时到场签到");
            log.info("[Reminder] 即将开始 → userId={}, reservationId={}, startTime={}",
                    r.getUserId(), r.getReservationId(), r.getStartTime());
        }
    }

    /**
     * 预约即将结束提醒
     * 每 60 秒扫描一次：end_time 在 [now+1min, now+15min] 内 且 status=occupied
     */
    @Scheduled(fixedDelay = 60_000)
    public void remindUpcomingEnd() {
        LocalDateTime now   = LocalDateTime.now();
        LocalDateTime from  = now.plusMinutes(1);
        LocalDateTime until = now.plusMinutes(REMIND_AHEAD_MINUTES);

        List<Reservation> expiring = reservationMapper.findByStatusAndEndTimeBetween(
                SystemConstants.ReservationStatus.OCCUPIED, from, until);

        for (Reservation r : expiring) {
            Seat seat = seatMapper.findById(r.getSeatId());
            if (seat == null) continue;
            String seatNumber = seat.getSeatNumber();

            // 去重：已发过此预约的结束提醒则跳过
            if (notificationMapper.findRecentReminderEnd(seatNumber) != null) {
                continue;
            }
            notificationService.send(
                    r.getUserId(),
                    SystemConstants.NotificationType.RESERVATION_REMINDER_END,
                    "您预约的 " + seatNumber + " 号座位即将结束，如需继续使用请重新预约");
            log.info("[Reminder] 即将结束 → userId={}, reservationId={}, endTime={}",
                    r.getUserId(), r.getReservationId(), r.getEndTime());
        }
    }
}
