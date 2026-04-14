package com.example.studyroom.controller;

import com.example.studyroom.po.StudyRoom;
import com.example.studyroom.service.StudyRoomService;
import com.example.studyroom.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 自习室管理 Controller
 * 基础路径：/rooms
 */
@RestController
@RequestMapping("/rooms")
public class StudyRoomController {

    @Autowired
    private StudyRoomService studyRoomService;

    /**
     * 获取所有自习室
     * GET /rooms
     */
    @GetMapping
    public Result<List<StudyRoom>> getAllRooms() {
        return Result.success(studyRoomService.getAllRooms());
    }

    /**
     * 根据 ID 获取自习室
     * GET /rooms/{id}
     */
    @GetMapping("/{id}")
    public Result<StudyRoom> getRoomById(@PathVariable("id") Integer id) {
        return Result.success(studyRoomService.getRoomById(id));
    }

    /**
     * 新增自习室
     * POST /rooms
     */
    @PostMapping
    public Result<StudyRoom> createRoom(@RequestBody StudyRoom room) {
        return Result.success(studyRoomService.createRoom(room));
    }

    /**
     * 更新自习室信息
     * PUT /rooms/{id}
     */
    @PutMapping("/{id}")
    public Result<StudyRoom> updateRoom(@PathVariable("id") Integer id, @RequestBody StudyRoom room) {
        return Result.success(studyRoomService.updateRoom(id, room));
    }

    /**
     * 删除自习室
     * DELETE /rooms/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteRoom(@PathVariable("id") Integer id) {
        studyRoomService.deleteRoom(id);
        return Result.success();
    }
}
