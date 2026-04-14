package com.example.studyroom.service;

import com.example.studyroom.po.Seat;
import com.example.studyroom.po.SeatRegion;

import java.util.List;

/**
 * 座位业务接口
 */
public interface SeatService {

    List<Seat> getAllSeats();

    Seat getSeatById(Integer seatId);

    /** 获取某自习室下的所有座位 */
    List<Seat> getSeatsByRoomId(Integer roomId);

    Seat createSeat(Seat seat);

    Seat updateSeat(Integer seatId, Seat seat);

    void deleteSeat(Integer seatId);

    /** 获取座位区域标定信息 */
    SeatRegion getRegionBySeatId(Integer seatId);

    /** 保存或更新座位区域标定（存在则更新，不存在则插入） */
    SeatRegion saveOrUpdateRegion(Integer seatId, SeatRegion region);
}
