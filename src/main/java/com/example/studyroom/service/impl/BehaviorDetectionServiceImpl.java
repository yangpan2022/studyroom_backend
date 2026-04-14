package com.example.studyroom.service.impl;

import com.example.studyroom.constant.SystemConstants;
import com.example.studyroom.mapper.BehaviorDetectionMapper;
import com.example.studyroom.mapper.NotificationMapper;
import com.example.studyroom.mapper.ReservationMapper;
import com.example.studyroom.mapper.SeatMapper;
import com.example.studyroom.po.BehaviorDetection;
import com.example.studyroom.po.Notification;
import com.example.studyroom.po.Reservation;
import com.example.studyroom.po.Seat;
import com.example.studyroom.service.BehaviorDetectionService;
import com.example.studyroom.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 行为检测业务实现类
 *
 * 通知联动逻辑：
 * 1. behaviorType = phone_detected 且 seat.status = occupied 时触发
 * 2. 精确定位当前时间区间内的有效预约用户
 * 3. 5 分钟内不重复发送同类通知（防轰炸）
 * 4. 置信度 < 0.8 视为误报，不触发通知
 */
@Service
public class BehaviorDetectionServiceImpl implements BehaviorDetectionService {

    /** 置信度阈值，低于此值视为误报不发通知 */
    private static final BigDecimal CONFIDENCE_THRESHOLD = new BigDecimal("0.80");

    @Autowired
    private BehaviorDetectionMapper behaviorDetectionMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    @Transactional
    public BehaviorDetection upload(BehaviorDetection detection) {
        // ─── 1. 参数校验 ─────────────────────────────────────────────────────────
        if (detection.getSeatId() == null) {
            throw new RuntimeException("seatId 不能为空");
        }
        if (detection.getBehaviorType() == null) {
            throw new RuntimeException("behaviorType 不能为空");
        }
        if (detection.getDetectTime() == null) {
            throw new RuntimeException("detectTime 不能为空");
        }

        Seat seat = seatMapper.findById(detection.getSeatId());
        if (seat == null) {
            throw new RuntimeException("座位不存在，ID: " + detection.getSeatId());
        }

        // ─── 2. 保存行为检测记录（无论后续是否发通知，记录都要入库）──────────────────
        behaviorDetectionMapper.insert(detection);

        // ─── 3. 联动通知逻辑 ─────────────────────────────────────────────────────
        if (SystemConstants.BehaviorType.PHONE_DETECTED.equals(detection.getBehaviorType())) {

            // 3a. 置信度过滤：低于 0.8 视为误报，不发通知
            if (detection.getConfidence() != null
                    && detection.getConfidence().compareTo(CONFIDENCE_THRESHOLD) < 0) {
                return detection;
            }

            // 3b. 座位必须处于 occupied 状态
            if (!SystemConstants.SeatStatus.OCCUPIED.equals(seat.getStatus())) {
                return detection;
            }

            // 3c. 精准定位当前有效用户（NOW() 时间过滤）
            Reservation activeReservation = reservationMapper.findActiveReservationBySeatId(detection.getSeatId());
            if (activeReservation == null) {
                return detection;
            }

            // 3d. 防重复通知：5 分钟内已发过 study_warning 则跳过
            Notification recentWarning = notificationMapper.findRecentWarning(activeReservation.getUserId());
            if (recentWarning != null) {
                return detection;
            }

            // 3e. 发送学习提醒通知
            notificationService.send(
                    activeReservation.getUserId(),
                    SystemConstants.NotificationType.STUDY_WARNING,
                    "检测到您可能正在使用手机，请注意专注学习");
        }

        return detection;
    }

    @Override
    public List<BehaviorDetection> getBySeatId(Integer seatId) {
        return behaviorDetectionMapper.findBySeatId(seatId);
    }
}
