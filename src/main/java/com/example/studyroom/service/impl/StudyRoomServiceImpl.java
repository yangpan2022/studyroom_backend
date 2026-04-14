package com.example.studyroom.service.impl;

import com.example.studyroom.constant.SystemConstants;
import com.example.studyroom.mapper.StudyRoomMapper;
import com.example.studyroom.po.StudyRoom;
import com.example.studyroom.service.StudyRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 自习室业务实现类
 */
@Service
public class StudyRoomServiceImpl implements StudyRoomService {

    @Autowired
    private StudyRoomMapper studyRoomMapper;

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
        studyRoomMapper.insert(room);
        return room;
    }

    @Override
    @Transactional
    public StudyRoom updateRoom(Integer roomId, StudyRoom room) {
        getRoomById(roomId); // 确认存在
        room.setRoomId(roomId);
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
