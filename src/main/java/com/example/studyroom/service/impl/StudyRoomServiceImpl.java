package com.example.studyroom.service.impl;

import com.example.studyroom.constant.SystemConstants;
import com.example.studyroom.mapper.SeatMapper;
import com.example.studyroom.mapper.StudyRoomMapper;
import com.example.studyroom.po.StudyRoom;
import com.example.studyroom.service.StudyRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 自习室业务实现类
 */
@Slf4j
@Service
public class StudyRoomServiceImpl implements StudyRoomService {

    @Autowired
    private StudyRoomMapper studyRoomMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Override
    public List<StudyRoom> getAllRooms() {
        return studyRoomMapper.findAll();
    }

    @Override
    public StudyRoom getRoomById(Integer roomId) {
        StudyRoom room = studyRoomMapper.findById(roomId);
        if (room == null) {
            throw new RuntimeException("自习室不存在，ID: " + roomId);
        }
        return room;
    }

    @Override
    @Transactional
    public StudyRoom createRoom(StudyRoom room) {
        if (room.getStatus() == null || room.getStatus().isBlank()) {
            room.setStatus(SystemConstants.RoomStatus.OPEN);
        }
        // videoType 默认 "video"，videoUrl 允许为空（前端自行提示）
        if (room.getVideoType() == null || room.getVideoType().isBlank()) {
            room.setVideoType("video");
        }
        log.info("[Room] Create room: name={}, videoUrl={}, videoType={}",
                room.getRoomName(), room.getVideoUrl(), room.getVideoType());
        studyRoomMapper.insert(room);
        return room;
    }

    @Override
    @Transactional
    public StudyRoom updateRoom(Integer roomId, StudyRoom room) {
        StudyRoom oldRoom = getRoomById(roomId); // 确认存在

        if (room.getCapacity() != null && room.getCapacity() < oldRoom.getCapacity()) {
            int currentCount = seatMapper.countByRoomId(roomId);
            if (room.getCapacity() < currentCount) {
                throw new RuntimeException("当前已有座位数超过新的容量限制，请先删除部分座位");
            }
        }

        room.setRoomId(roomId);
        if (room.getVideoType() == null || room.getVideoType().isBlank()) {
            room.setVideoType("video");
        }
        studyRoomMapper.update(room);
        return room;
    }

    @Override
    @Transactional
    public void deleteRoom(Integer roomId) {
        getRoomById(roomId);
        studyRoomMapper.deleteById(roomId);
    }
}
