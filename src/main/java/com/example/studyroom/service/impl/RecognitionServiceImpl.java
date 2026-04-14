package com.example.studyroom.service.impl;

import com.example.studyroom.constant.SystemConstants;
import com.example.studyroom.mapper.NotificationMapper;
import com.example.studyroom.mapper.RecognitionRecordMapper;
import com.example.studyroom.mapper.ReservationMapper;
import com.example.studyroom.mapper.SeatMapper;
import com.example.studyroom.po.RecognitionRecord;
import com.example.studyroom.po.Reservation;
import com.example.studyroom.po.Seat;
import com.example.studyroom.service.NotificationService;
import com.example.studyroom.service.RecognitionService;
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

        // 2. occupied=false：自动释放逻辑（原有逻辑不变）
        if (Boolean.FALSE.equals(record.getOccupied())) {
            Seat seat = seatMapper.findById(record.getSeatId());

            if (seat != null && SystemConstants.SeatStatus.OCCUPIED.equals(seat.getStatus())) {

                // 2a. 释放座位 -> available
                seatMapper.updateStatus(record.getSeatId(), SystemConstants.SeatStatus.AVAILABLE);

                // 2b. 同步完成对应预约记录 -> completed
                Reservation activeReservation = reservationMapper.findActiveReservationBySeatId(record.getSeatId());
                if (activeReservation != null) {
                    reservationMapper.updateStatus(
                            activeReservation.getReservationId(),
                            SystemConstants.ReservationStatus.COMPLETED);

                    // 2c. 通知用户：座位自动释放
                    notificationService.send(
                            activeReservation.getUserId(),
                            SystemConstants.NotificationType.SEAT_AUTO_RELEASED,
                            "系统检测到座位已无人使用，座位（ID：" + record.getSeatId() + "）已自动释放，预约已完成");
                }
            }
            return record;
        }

        // 3. occupied=true：异常占用检测
        // 该场景处于 "AI 检测到有人，但预约状态仍是 reserved（未签到）"
        if (Boolean.TRUE.equals(record.getOccupied())) {
            // 3a. 查询当前时间窗口内是否存在 reserved 预约
            Reservation reservation = reservationMapper.findActiveReservationBySeatId(record.getSeatId());
            if (reservation == null) {
                // 无有效预约，正常情况（未预约直接坐），无需处理
                return record;
            }

            // 3b. 缓冲期判断：预约开始后 5 分钟内不发送，防止误判"用户刚到还没签到"
            LocalDateTime graceDeadline = reservation.getStartTime().plusMinutes(CHECKIN_GRACE_MINUTES);
            if (LocalDateTime.now().isBefore(graceDeadline)) {
                // 还在缓冲期内，暂不通知
                return record;
            }

            // 3c. 防重复通知：5 分钟内同用户已发过 seat_conflict_warning 则跳过
            // 仅用 userId + type + 时间窗口判断，不解析 message 文本，安全可靠
            if (notificationMapper.findRecentConflictWarning(reservation.getUserId()) != null) {
                return record;
            }

            // 3d. 发送异常占用提醒
            notificationService.send(
                    reservation.getUserId(),
                    SystemConstants.NotificationType.SEAT_CONFLICT_WARNING,
                    "检测到您的预约座位（ID：" + record.getSeatId() + "）可能被他人占用，请及时签到或确认");
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

