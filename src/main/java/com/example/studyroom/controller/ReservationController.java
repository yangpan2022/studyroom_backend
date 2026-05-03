package com.example.studyroom.controller;

import com.example.studyroom.dto.ReservationDTO;
import com.example.studyroom.dto.UpdateReservationDTO;
import com.example.studyroom.po.Reservation;
import com.example.studyroom.service.ReservationService;
import com.example.studyroom.vo.ReservationVO;
import com.example.studyroom.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约管理 Controller
 * 基础路径：/reservations
 *
 * 状态流转模型：
 * 1. 预约: POST /reservations  -> reservation:reserved, seat:reserved
 * 2. 签到: PUT /{id}/checkin   -> reservation:occupied, seat:occupied
 * 3. 签退: PUT /{id}/checkout  -> reservation:completed, seat:available
 * 4. 取消: PUT /{id}/cancel    -> reservation:cancelled, seat:available
 */
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * 查询预约列表（含位置信息）
     * GET /reservations?userId=&seatId=
     */
    @GetMapping
    public Result<List<ReservationVO>> getReservations(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "seatId", required = false) Integer seatId) {

        if (userId != null) {
            return Result.success(reservationService.getReservationVOsByUserId(userId));
        }
        if (seatId != null) {
            return Result.success(reservationService.getReservationVOsBySeatId(seatId));
        }
        return Result.success(reservationService.getAllReservationVOs());
    }

    /**
     * 查询预约详情（含位置信息）
     * GET /reservations/{id}
     */
    @GetMapping("/{id}")
    public Result<ReservationVO> getReservationById(@PathVariable("id") Integer id) {
        return Result.success(reservationService.getReservationVOById(id));
    }

    /**
     * 创建预约
     * 校验 startTime 必须晚于当前时间
     */
    @PostMapping
    public Result<Reservation> createReservation(@RequestBody @Valid ReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        reservation.setUserId(reservationDTO.getUserId());
        reservation.setSeatId(reservationDTO.getSeatId());
        reservation.setStartTime(reservationDTO.getStartTime());
        reservation.setEndTime(reservationDTO.getEndTime());
        return Result.success(reservationService.createReservation(reservation));
    }

    /**
     * 修改预约（座位 / 时间）
     * PUT /reservations/{id}
     */
    @PutMapping("/{id}")
    public Result<Reservation> updateReservation(@PathVariable("id") Integer id, @RequestBody @Valid com.example.studyroom.dto.UpdateReservationDTO dto) {
        return Result.success(reservationService.updateReservation(id, dto.getSeatId(), dto.getStartTime(), dto.getEndTime()));
    }

    /**
     * 取消预约
     */
    @PutMapping("/{id}/cancel")
    public Result<Reservation> cancelReservation(@PathVariable("id") Integer id) {
        return Result.success(reservationService.cancelReservation(id));
    }

    /**
     * 签到
     * 触发座位状态变为 occupied
     */
    @PutMapping("/{id}/checkin")
    public Result<Reservation> checkIn(@PathVariable("id") Integer id) {
        return Result.success(reservationService.checkIn(id));
    }

    /**
     * 签退/结速
     * 触发座位状态变为 available，预约记录变为 completed
     */
    @PutMapping("/{id}/checkout")
    public Result<Reservation> checkOut(@PathVariable("id") Integer id) {
        return Result.success(reservationService.checkOut(id));
    }

    /**
     * 完成预约 (同 checkOut)
     */
    @PutMapping("/{id}/complete")
    public Result<Reservation> completeReservation(@PathVariable("id") Integer id) {
        return Result.success(reservationService.completeReservation(id));
    }
}
