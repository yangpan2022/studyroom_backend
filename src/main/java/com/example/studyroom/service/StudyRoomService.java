package com.example.studyroom.service;

import com.example.studyroom.po.StudyRoom;

import java.util.List;

/**
 * 自习室业务接口
 */
public interface StudyRoomService {

    List<StudyRoom> getAllRooms();

    StudyRoom getRoomById(Integer roomId);

    StudyRoom createRoom(StudyRoom room);

    StudyRoom updateRoom(Integer roomId, StudyRoom room);

    void deleteRoom(Integer roomId);
}
