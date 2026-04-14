package com.example.studyroom.service;

import com.example.studyroom.po.RecognitionRecord;

import java.util.List;

/**
 * AI 识别业务接口
 */
public interface RecognitionService {

    /**
     * AI 模块上传识别结果
     * 保存记录后，根据识别结果触发联动逻辑（如自动释放座位）
     */
    RecognitionRecord uploadRecord(RecognitionRecord record);

    /** 查询所有识别记录 */
    List<RecognitionRecord> getAllRecords();

    /** 根据座位 ID 查询识别历史 */
    List<RecognitionRecord> getRecordsBySeatId(Integer seatId);
}
