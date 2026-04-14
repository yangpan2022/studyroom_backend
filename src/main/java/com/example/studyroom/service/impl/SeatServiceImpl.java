package com.example.studyroom.service.impl;

import com.example.studyroom.constant.SystemConstants;
import com.example.studyroom.mapper.SeatMapper;
import com.example.studyroom.mapper.SeatRegionMapper;
import com.example.studyroom.po.Seat;
import com.example.studyroom.po.SeatRegion;
import com.example.studyroom.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 座位业务实现类
 */
@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private SeatRegionMapper seatRegionMapper;

    @Override
    public List<Seat> getAllSeats() {
        return seatMapper.findAll();
    }

    @Override
    public Seat getSeatById(Integer seatId) {
        Seat seat = seatMapper.findById(seatId);
        if (seat == null) {
            throw new RuntimeException("座位不存在，ID: " + seatId);
        }
        return seat;
    }

    @Override
    public List<Seat> getSeatsByRoomId(Integer roomId) {
        return seatMapper.findByRoomId(roomId);
    }

    @Override
    @Transactional
    public Seat createSeat(Seat seat) {
        if (seat.getStatus() == null || seat.getStatus().isBlank()) {
            seat.setStatus(SystemConstants.SeatStatus.AVAILABLE);
        }
        seatMapper.insert(seat);
        return seat;
    }

    @Override
    @Transactional
    public Seat updateSeat(Integer seatId, Seat seat) {
        getSeatById(seatId); // 确认存在
        seat.setSeatId(seatId);
        seatMapper.update(seat);
        return seat;
    }

    @Override
    @Transactional
    public void deleteSeat(Integer seatId) {
        getSeatById(seatId);
        seatMapper.deleteById(seatId);
    }

    @Override
    public SeatRegion getRegionBySeatId(Integer seatId) {
        getSeatById(seatId); // 确认座位存在
        return seatRegionMapper.findBySeatId(seatId);
    }

    @Override
    @Transactional
    public SeatRegion saveOrUpdateRegion(Integer seatId, SeatRegion region) {
        getSeatById(seatId); // 确认座位存在
        region.setSeatId(seatId);
        SeatRegion existing = seatRegionMapper.findBySeatId(seatId);
        if (existing == null) {
            seatRegionMapper.insert(region);
        } else {
            seatRegionMapper.updateBySeatId(region);
        }
        return region;
    }
}
