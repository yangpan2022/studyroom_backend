package com.example.studyroom.controller;

import com.example.studyroom.po.Seat;
import com.example.studyroom.po.SeatRegion;
import com.example.studyroom.service.SeatService;
import com.example.studyroom.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 座位管理 Controller
 * 基础路径：/seats
 * 嵌套路径：/rooms/{roomId}/seats（按自习室查询座位）
 */
@RestController
public class SeatController {

    @Autowired
    private SeatService seatService;

    /**
     * 获取所有座位
     * GET /seats
     */
    @GetMapping("/seats")
    public Result<List<Seat>> getAllSeats() {
        return Result.success(seatService.getAllSeats());
    }

    /**
     * 根据 ID 获取座位
     * GET /seats/{id}
     */
    @GetMapping("/seats/{id}")
    public Result<Seat> getSeatById(@PathVariable("id") Integer id) {
        return Result.success(seatService.getSeatById(id));
    }

    /**
     * 获取某自习室下的所有座位
     * GET /rooms/{roomId}/seats
     */
    @GetMapping("/rooms/{roomId}/seats")
    public Result<List<Seat>> getSeatsByRoom(@PathVariable("roomId") Integer roomId) {
        return Result.success(seatService.getSeatsByRoomId(roomId));
    }

    /**
     * 新增座位
     * POST /seats
     */
    @PostMapping("/seats")
    public Result<Seat> createSeat(@RequestBody Seat seat) {
        return Result.success(seatService.createSeat(seat));
    }

    /**
     * 更新座位信息
     * PUT /seats/{id}
     */
    @PutMapping("/seats/{id}")
    public Result<Seat> updateSeat(@PathVariable("id") Integer id, @RequestBody Seat seat) {
        return Result.success(seatService.updateSeat(id, seat));
    }

    /**
     * 删除座位
     * DELETE /seats/{id}
     */
    @DeleteMapping("/seats/{id}")
    public Result<Void> deleteSeat(@PathVariable("id") Integer id) {
        seatService.deleteSeat(id);
        return Result.success();
    }

    /**
     * 获取座位的摄像头区域标定信息
     * GET /seats/{id}/region
     */
    @GetMapping("/seats/{id}/region")
    public Result<SeatRegion> getRegion(@PathVariable("id") Integer id) {
        return Result.success(seatService.getRegionBySeatId(id));
    }

    /**
     * 保存或更新座位的摄像头区域标定（管理端标定使用）
     * PUT /seats/{id}/region
     */
    @PutMapping("/seats/{id}/region")
    public Result<SeatRegion> saveOrUpdateRegion(@PathVariable("id") Integer id, @RequestBody SeatRegion region) {
        return Result.success(seatService.saveOrUpdateRegion(id, region));
    }
}
