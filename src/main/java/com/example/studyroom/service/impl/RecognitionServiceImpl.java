package com.example.studyroom.service.impl;

import com.example.studyroom.constant.SystemConstants;
import com.example.studyroom.mapper.NotificationMapper;
import com.example.studyroom.mapper.RecognitionRecordMapper;
import com.example.studyroom.mapper.ReservationMapper;
import com.example.studyroom.mapper.SeatMapper;
import com.example.studyroom.po.Notification;
import com.example.studyroom.po.RecognitionRecord;
import com.example.studyroom.po.Reservation;
import com.example.studyroom.po.Seat;
import com.example.studyroom.service.NotificationService;
import com.example.studyroom.service.RecognitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 识别业务实现类
 *
 * 自动释放逻辑（完整版）：
 * 当 AI 上传 occupied=false，且座位当前 status=occupied 时，执行：
 * 1. seat.status -> available
 * 2. 对应 reservation.status -> completed
 * 3. 发送通知：seat_auto_released
 *
 * 异常占用提醒逻辑：
 * 当 AI 上传 occupied=true，且存在 status=reserved 的有效预约时，执行：
 * 1. 缓冲期内（预约开始后 5 分钟内）不发送，避免误判用户刚到座位
 * 2. 5 分钟内同用户不重复发送 seat_conflict_warning
 * 3. 发送通知：seat_conflict_warning
 */
@Slf4j
@Service
public class RecognitionServiceImpl implements RecognitionService {

    /** 预约开始后的缓冲时间（分钟），避免用户刚到座位还没来得及签到时被误判 */
    private static final long CHECKIN_GRACE_MINUTES = 5L;

    @Autowired
    private RecognitionRecordMapper recognitionRecordMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    @Transactional
    public RecognitionRecord uploadRecord(RecognitionRecord record) {
        // 1. 保存识别记录
        recognitionRecordMapper.insert(record);
        log.info("[Recognition] 记录入库 seatId={}, occupied={}, confidence={}",
                record.getSeatId(), record.getOccupied(), record.getConfidence());

        // 2. occupied=false：自动释放逻辑（原有逻辑不变）
        if (Boolean.FALSE.equals(record.getOccupied())) {
            Seat seat = seatMapper.findById(record.getSeatId());

            if (seat != null && SystemConstants.SeatStatus.OCCUPIED.equals(seat.getStatus())) {

                // 引入阈值控制：查询最近 5 条记录
                List<RecognitionRecord> recentRecords = recognitionRecordMapper.findRecentRecordsByCount(record.getSeatId(), 5);
                boolean allFalse = true;
                for (RecognitionRecord r : recentRecords) {
                    if (Boolean.TRUE.equals(r.getOccupied())) {
                        allFalse = false;
                        break;
                    }
                }

                if (!allFalse) {
                    log.info("[Recognition] seatId={} 离座缓冲：最近 5 条记录中存在有人判定，暂不释放座位", record.getSeatId());
                    return record;
                }

                // 2a. 释放座位 -> available
                seatMapper.updateStatus(record.getSeatId(), SystemConstants.SeatStatus.AVAILABLE);

                // 2b. 同步完成对应预约记录 -> completed
                Reservation activeReservation = reservationMapper.findActiveReservationBySeatId(record.getSeatId());
                if (activeReservation != null) {
                    reservationMapper.updateStatus(
                            activeReservation.getReservationId(),
                            SystemConstants.ReservationStatus.COMPLETED);

                    String seatNumber = seat.getSeatNumber();
                    // 2c. 通知用户：座位自动释放
                    notificationService.send(
                            activeReservation.getUserId(),
                            SystemConstants.NotificationType.SEAT_AUTO_RELEASED,
                            "系统检测到 " + seatNumber + " 号座位已无人使用，已自动释放该座位并完成预约。");
                }
            }
            return record;
        }

        // 3. occupied=true：异常占用检测（含完整诊断日志）
        if (Boolean.TRUE.equals(record.getOccupied())) {
            log.info("[Recognition] ── occupied=true 分支 ── seatId={}", record.getSeatId());

            // 3a. 查询当前有效预约
            Reservation reservation = reservationMapper.findActiveReservationBySeatId(record.getSeatId());
            if (reservation == null) {
                log.warn("[Recognition] ⚠ findActiveReservationBySeatId({}) = null"
                        + " → 不发通知。排查：① status IN ('reserved','occupied')"
                        + " ② NOW() 在 start_time~end_time 之间 ③ 数据库时区",
                        record.getSeatId());
                return record;
            }
            log.info("[Recognition] 找到预约 reservationId={}, userId={}, status={}, startTime={}, endTime={}",
                    reservation.getReservationId(), reservation.getUserId(), reservation.getStatus(),
                    reservation.getStartTime(), reservation.getEndTime());

            // 3b. 缓冲期判断
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime graceDeadline = reservation.getStartTime().plusMinutes(CHECKIN_GRACE_MINUTES);
            boolean inGrace = now.isBefore(graceDeadline);
            log.info("[Recognition] 时间诊断: now={}, graceDeadline={}, inGracePeriod={}",
                    now, graceDeadline, inGrace);
            if (inGrace) {
                log.warn("[Recognition] ⚠ 仍在 {}min 缓冲期内 → 跳出", CHECKIN_GRACE_MINUTES);
                return record;
            }

            // 3c. 防重复
            Notification recent = notificationMapper.findRecentConflictWarning(reservation.getUserId());
            log.info("[Recognition] findRecentConflictWarning(userId={}) = {}",
                    reservation.getUserId(),
                    recent == null ? "null（可发送）"
                            : "已有记录 id=" + recent.getNotificationId() + " sendTime=" + recent.getSendTime());
            if (recent != null) {
                log.warn("[Recognition] ⚠ 5min 内已发过 seat_conflict_warning → 跳出");
                return record;
            }

            // 3d. 发送通知
            Seat seatInfo = seatMapper.findById(record.getSeatId());
            String seatNumber = seatInfo != null ? seatInfo.getSeatNumber() : String.valueOf(record.getSeatId());
            notificationService.send(
                    reservation.getUserId(),
                    SystemConstants.NotificationType.SEAT_CONFLICT_WARNING,
                    "您预约的 " + seatNumber + " 号座位可能被他人占用，请及时确认或尽快签到。");
            log.info("[Recognition] ✅ seat_conflict_warning 已发送 → userId={}", reservation.getUserId());
        }

        return record;
    }

    @Override
    public List<RecognitionRecord> getAllRecords() {
        return recognitionRecordMapper.findAll();
    }

    @Override
    public List<RecognitionRecord> getRecordsBySeatId(Integer seatId) {
        return recognitionRecordMapper.findBySeatId(seatId);
    }
}
