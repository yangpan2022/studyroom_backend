package com.example.studyroom.service;

import com.example.studyroom.po.Reservation;

import java.util.List;

/**
 * 预约业务接口
 *
 * 状态流转（与 DB 对齐）：
 * reserved → cancelled（取消）
 * reserved → completed（完成）
 */
public interface ReservationService {

    List<Reservation> getAllReservations();

    Reservation getReservationById(Integer reservationId);

    List<Reservation> getReservationsByUserId(Integer userId);

    List<Reservation> getReservationsBySeatId(Integer seatId);

    /**
     * 创建预约（插入记录，seat → reserved）
     */
    Reservation createReservation(Reservation reservation);

    /**
     * 取消预约（reservation → cancelled，seat → available）
     */
    Reservation cancelReservation(Integer reservationId);

    /**
     * 完成预约（reservation → completed，seat → available）
     */
    Reservation completeReservation(Integer reservationId);

    // 兼容旧接口签名（内部转发到 completeReservation）
    Reservation checkIn(Integer reservationId);

    Reservation checkOut(Integer reservationId);
}
