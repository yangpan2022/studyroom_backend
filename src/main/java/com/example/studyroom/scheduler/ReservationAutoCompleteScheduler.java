package com.example.studyroom.scheduler;

import com.example.studyroom.constant.SystemConstants;
import com.example.studyroom.mapper.ReservationMapper;
import com.example.studyroom.mapper.SeatMapper;
import com.example.studyroom.po.Reservation;
import com.example.studyroom.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 预约自动签退定时任务
 *
 * 触发条件：
 *   reservation.status = "occupied" AND end_time < NOW()
 *
 * 每次执行：
 *   1. reservation.status → completed
 *   2. seat.status → available
 *   3. 发送通知：reservation_completed_auto
 *
 * 去重机制：
 *   状态流转保证幂等 —— 处理完成后 status=completed，下次扫描不会再命中
 */
@Slf4j
@Component
public class ReservationAutoCompleteScheduler {

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private NotificationService notificationService;

    /**
     * 每 60 秒扫描一次，对所有"已到期但未签退"的预约执行自动签退
     */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void autoComplete() {
        List<Reservation> expired = reservationMapper.findExpiredOccupied();
        if (expired.isEmpty()) return;

        log.info("[AutoComplete] 发现 {} 条过期未签退预约，开始自动处理", expired.size());

        for (Reservation r : expired) {
            try {
                // 1. 更新预约状态 → completed
                reservationMapper.updateStatus(
                        r.getReservationId(),
                        SystemConstants.ReservationStatus.COMPLETED);

                // 2. 释放座位 → available
                seatMapper.updateStatus(
                        r.getSeatId(),
                        SystemConstants.SeatStatus.AVAILABLE);

                // 3. 发送通知
                com.example.studyroom.po.Seat seat = seatMapper.findById(r.getSeatId());
                String seatNumber = seat != null ? seat.getSeatNumber() : String.valueOf(r.getSeatId());
                notificationService.send(
                        r.getUserId(),
                        SystemConstants.NotificationType.RESERVATION_COMPLETED_AUTO,
                        "您的预约已结束，系统已自动为您签退，" + seatNumber + " 号座位已释放。");

                log.info("[AutoComplete] 已处理 reservationId={}, seatId={}, userId={}, endTime={}",
                        r.getReservationId(), r.getSeatId(), r.getUserId(), r.getEndTime());

            } catch (Exception e) {
                // 单条失败不影响其余记录，记录日志后继续
                log.error("[AutoComplete] 处理 reservationId={} 失败：{}",
                        r.getReservationId(), e.getMessage(), e);
            }
        }
    }
}
