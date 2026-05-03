package com.example.studyroom.mapper;

import com.example.studyroom.po.StudyRoom;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 自习室 Mapper 接口（MyBatis 注解方式）
 */
@Mapper
public interface StudyRoomMapper {

    @Select("SELECT room_id AS roomId, room_name AS roomName, location, capacity, status, " +
            "video_url AS videoUrl, video_type AS videoType " +
            "FROM study_room")
    List<StudyRoom> findAll();

    @Select("SELECT room_id AS roomId, room_name AS roomName, location, capacity, status, " +
            "video_url AS videoUrl, video_type AS videoType " +
            "FROM study_room WHERE room_id = #{roomId}")
    StudyRoom findById(Integer roomId);

    @Insert("INSERT INTO study_room(room_name, location, capacity, status, video_url, video_type) " +
            "VALUES(#{roomName}, #{location}, #{capacity}, #{status}, #{videoUrl}, #{videoType})")
    @Options(useGeneratedKeys = true, keyProperty = "roomId")
    int insert(StudyRoom room);

    @Update("UPDATE study_room SET room_name=#{roomName}, location=#{location}, capacity=#{capacity}, " +
            "status=#{status}, video_url=#{videoUrl}, video_type=#{videoType} " +
            "WHERE room_id=#{roomId}")
    int update(StudyRoom room);

    @Delete("DELETE FROM study_room WHERE room_id = #{roomId}")
    int deleteById(Integer roomId);
}
