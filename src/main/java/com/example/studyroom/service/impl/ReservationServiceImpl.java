package com.example.studyroom.service.impl;

import com.example.studyroom.constant.SystemConstants;
import com.example.studyroom.mapper.ReservationMapper;
import com.example.studyroom.mapper.SeatMapper;
import com.example.studyroom.mapper.UserMapper;
import com.example.studyroom.po.Reservation;
import com.example.studyroom.po.Seat;
import com.example.studyroom.service.NotificationService;
import com.example.studyroom.service.ReservationService;
import com.example.studyroom.vo.ReservationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约业务实现类
 *
 * 状态流转设计：
 * 1. 创建预约: reservation=reserved, seat=reserved
 * 2. 签到 (checkIn): seat=occupied (reservation 不变)
 * 3. 签退/完成 (checkOut/complete): reservation=completed, seat=available
 * 4. 取消 (cancel): reservation=cancelled, seat=available
 */
@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public List<Reservation> getAllReservations() {
        return reservationMapper.findAll();
    }

    @Override
    public Reservation getReservationById(Integer reservationId) {
        Reservation reservation = reservationMapper.findById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在，ID: " + reservationId);
        }
        return reservation;
    }

    @Override
    public List<Reservation> getReservationsByUserId(Integer userId) {
        return reservationMapper.findByUserId(userId);
    }

    @Override
    public List<Reservation> getReservationsBySeatId(Integer seatId) {
        return reservationMapper.findBySeatId(seatId);
    }

    // ─── VO 方法（用于前端展示，含位置信息）──────────────────────────────────────

    @Override
    public List<ReservationVO> getAllReservationVOs() {
        return reservationMapper.findAllVO();
    }

    @Override
    public ReservationVO getReservationVOById(Integer reservationId) {
        return reservationMapper.findVOById(reservationId);
    }

    @Override
    public List<ReservationVO> getReservationVOsByUserId(Integer userId) {
        return reservationMapper.findVOByUserId(userId);
    }

    @Override
    public List<ReservationVO> getReservationVOsBySeatId(Integer seatId) {
        return reservationMapper.findVOBySeatId(seatId);
    }

    /**
     * 创建预约
     * 成功后发送通知：reservation_success
     */
    @Override
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        // 1. 基础校验
        if (reservation.getStartTime() == null || reservation.getEndTime() == null) {
            throw new RuntimeException("预约时间不能为空");
        }
        if (!reservation.getEndTime().isAfter(reservation.getStartTime())) {
            throw new RuntimeException("结束时间必须晚于开始时间");
        }

        // 2. 时间校验：必须晚于当前时间
        if (reservation.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("预约开始时间不能早于当前时间");
        }

        // 2.5 后端核心拦截：防止一个学生同时占用/预约多个座位
        if (reservationMapper.countActiveByUserId(reservation.getUserId()) > 0) {
            throw new RuntimeException("您当前已有未完成的预约，请先结束或取消后再试");
        }

        // 3. 用户与座位存在性校验
        if (userMapper.findById(reservation.getUserId()) == null) {
            throw new RuntimeException("用户不存在");
        }
        Seat seat = seatMapper.findById(reservation.getSeatId());
        if (seat == null) {
            throw new RuntimeException("座位不存在");
        }
        if (!SystemConstants.SeatStatus.AVAILABLE.equals(seat.getStatus())) {
            throw new RuntimeException("该座位当前不可预约，状态：" + seat.getStatus());
        }

        // 4. 冲突检测
        List<Reservation> conflicts = reservationMapper.findConflictReservations(
                reservation.getSeatId(), reservation.getStartTime(), reservation.getEndTime());
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("该时间段内座位已被预约，存在时间冲突");
        }

        // 5. 保存预约
        reservation.setStatus(SystemConstants.ReservationStatus.RESERVED);
        reservationMapper.insert(reservation);

        // 6. 座位状态 -> reserved
        seatMapper.updateStatus(reservation.getSeatId(), SystemConstants.SeatStatus.RESERVED);

        // 7. 通知用户：预约成功
        notificationService.send(
                reservation.getUserId(),
                SystemConstants.NotificationType.RESERVATION_SUCCESS,
                "您已成功预约座位（座位ID：" + reservation.getSeatId() + "），预约时间：" +
                        reservation.getStartTime() + " ~ " + reservation.getEndTime());

        return reservation;
    }

    /**
     * 修改预约（支持修改时间、座位）
     */
    @Override
    @Transactional
    public Reservation updateReservation(Integer reservationId, Integer seatId, LocalDateTime startTime,
            LocalDateTime endTime) {
        Reservation reservation = getReservationById(reservationId);

        // 1. 只有 reserved 状态允许修改
        if (!SystemConstants.ReservationStatus.RESERVED.equals(reservation.getStatus())) {
            throw new RuntimeException("当前预约状态不可修改，状态：" + reservation.getStatus());
        }

        // 2. 时间校验
        if (startTime == null || endTime == null) {
            throw new RuntimeException("预约时间不能为空");
        }
        if (!endTime.isAfter(startTime)) {
            throw new RuntimeException("开始时间必须早于结束时间");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("预约开始时间必须晚于当前时间");
        }

        // 3. 座位及新状态合法性校验
        Seat seat = seatMapper.findById(seatId);
        if (seat == null) {
            throw new RuntimeException("座位不存在");
        }
        // 如果更换了座位，新座位必须是 available 状态
        if (!reservation.getSeatId().equals(seatId) && !SystemConstants.SeatStatus.AVAILABLE.equals(seat.getStatus())) {
            throw new RuntimeException("该座位当前不可选择，状态：" + seat.getStatus());
        }

        // 4. 新的冲突检测（排除自己）
        List<Reservation> conflicts = reservationMapper.findConflictReservationsExcluding(
                seatId, startTime, endTime, reservationId);
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("该时间段内座位已被预约，存在时间冲突");
        }

        // 5. 更换座位时的状态联动
        if (!reservation.getSeatId().equals(seatId)) {
            // 原座位释放
            seatMapper.updateStatus(reservation.getSeatId(), SystemConstants.SeatStatus.AVAILABLE);
            // 新座位锁定
            seatMapper.updateStatus(seatId, SystemConstants.SeatStatus.RESERVED);
        }

        // 6. 核心更新逻辑
        reservationMapper.updateReservationProcess(reservationId, seatId, startTime, endTime);

        // 同步返回对象
        reservation.setSeatId(seatId);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);

        return reservation;
    }

    /**
     * 签到逻辑
     * 校验时间窗口 [startTime-5min, endTime]
     * seat.status: reserved -> occupied
     * 成功后发送通知：checkin_success
     */
    @Override
    @Transactional
    public Reservation checkIn(Integer reservationId) {
        Reservation reservation = getReservationById(reservationId);

        // 1. 预约状态必须是 reserved
        if (!SystemConstants.ReservationStatus.RESERVED.equals(reservation.getStatus())) {
            throw new RuntimeException("当前预约不是 reserved 状态，无法签到，状态：" + reservation.getStatus());
        }

        // 2. 座位状态必须是 reserved（防止重复签到）
        Seat seat = seatMapper.findById(reservation.getSeatId());
        if (!SystemConstants.SeatStatus.RESERVED.equals(seat.getStatus())) {
            throw new RuntimeException("座位当前不是 reserved 状态，无法签到，座位状态：" + seat.getStatus());
        }

        // 3. 时间窗口校验：当前时间必须在 [startTime-5min, endTime] 内
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkInWindowStart = reservation.getStartTime().minusMinutes(5);
        if (now.isBefore(checkInWindowStart)) {
            throw new RuntimeException("签到时间过早，请于预约开始时间前 5 分钟后再签到");
        }
        if (now.isAfter(reservation.getEndTime())) {
            throw new RuntimeException("预约已过期，无法签到");
        }

        // 4. 更新座位状态 -> occupied
        seatMapper.updateStatus(reservation.getSeatId(), SystemConstants.SeatStatus.OCCUPIED);

        // 4b. 同步预约状态 -> occupied（体现"使用中"，与签到前的 reserved 区分）
        reservationMapper.updateStatus(reservationId, SystemConstants.ReservationStatus.OCCUPIED);
        reservation.setStatus(SystemConstants.ReservationStatus.OCCUPIED);

        // 5. 通知用户：签到成功
        notificationService.send(
                reservation.getUserId(),
                SystemConstants.NotificationType.CHECKIN_SUCCESS,
                "您已成功签到（座位ID：" + reservation.getSeatId() + "），开始使用");

        return reservation;
    }

    /**
     * 签退/完成逻辑
     * seat.status 必须是 occupied（防止未签到就完成）
     * reservation.status -> completed, seat.status -> available
     * 成功后发送通知：reservation_completed
     */
    @Override
    @Transactional
    public Reservation completeReservation(Integer reservationId) {
        Reservation reservation = getReservationById(reservationId);

        // 1. 预约必须处于 occupied（已签到才能签退）
        if (!SystemConstants.ReservationStatus.OCCUPIED.equals(reservation.getStatus())) {
            throw new RuntimeException("请先签到后再签退，当前预约状态：" + reservation.getStatus());
        }

        // 2. 座位必须处于 occupied（防止未签到就签退）
        Seat seat = seatMapper.findById(reservation.getSeatId());
        if (!SystemConstants.SeatStatus.OCCUPIED.equals(seat.getStatus())) {
            throw new RuntimeException("座位当前不处于使用中状态，请先签到再签退，座位状态：" + seat.getStatus());
        }

        reservationMapper.updateStatus(reservationId, SystemConstants.ReservationStatus.COMPLETED);
        seatMapper.updateStatus(reservation.getSeatId(), SystemConstants.SeatStatus.AVAILABLE);

        // 3. 通知用户：完成预约
        notificationService.send(
                reservation.getUserId(),
                SystemConstants.NotificationType.RESERVATION_COMPLETED,
                "您的预约已完成（座位ID：" + reservation.getSeatId() + "），座位已释放");

        reservation.setStatus(SystemConstants.ReservationStatus.COMPLETED);
        return reservation;
    }

    @Override
    @Transactional
    public Reservation checkOut(Integer reservationId) {
        return completeReservation(reservationId);
    }

    /**
     * 取消预约
     * reservation.status -> cancelled, seat.status -> available
     * 成功后发送通知：reservation_cancelled
     */
    @Override
    @Transactional
    public Reservation cancelReservation(Integer reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (!SystemConstants.ReservationStatus.RESERVED.equals(reservation.getStatus())) {
            throw new RuntimeException("当前预约状态无法取消，状态：" + reservation.getStatus());
        }

        reservationMapper.updateStatus(reservationId, SystemConstants.ReservationStatus.CANCELLED);
        seatMapper.updateStatus(reservation.getSeatId(), SystemConstants.SeatStatus.AVAILABLE);

        // 通知用户：预约已取消
        notificationService.send(
                reservation.getUserId(),
                SystemConstants.NotificationType.RESERVATION_CANCELLED,
                "您的预约已取消（座位ID：" + reservation.getSeatId() + "）");

        reservation.setStatus(SystemConstants.ReservationStatus.CANCELLED);
        return reservation;
    }
}
