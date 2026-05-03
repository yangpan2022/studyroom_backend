package com.example.studyroom.service;

import com.example.studyroom.po.Reservation;
import com.example.studyroom.vo.ReservationVO;

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

    /** 查询全部预约 VO（含位置信息，用于前端列表展示） */
    List<ReservationVO> getAllReservationVOs();

    /** 查询单条预约 VO（含位置信息，用于详情展示） */
    ReservationVO getReservationVOById(Integer reservationId);

    /** 根据 userId 查询该用户预约 VO 列表 */
    List<ReservationVO> getReservationVOsByUserId(Integer userId);

    /** 根据 seatId 查询该座位预约 VO 列表 */
    List<ReservationVO> getReservationVOsBySeatId(Integer seatId);

    /**
     * 创建预约（插入记录，seat → reserved）
     */
    Reservation createReservation(Reservation reservation);

    /**
     * 修改预约（seatId / startTime / endTime）
     */
    Reservation updateReservation(Integer reservationId, Integer seatId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);

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
