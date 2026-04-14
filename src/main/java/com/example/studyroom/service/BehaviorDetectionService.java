package com.example.studyroom.service;

import com.example.studyroom.po.BehaviorDetection;

import java.util.List;

/**
 * 行为检测业务接口
 */
public interface BehaviorDetectionService {

    /**
     * 上传行为检测记录
     * 如果行为是 phone_detected，则联动发送通知
     */
    BehaviorDetection upload(BehaviorDetection detection);

    /**
     * 查询座位的行为记录
     */
    List<BehaviorDetection> getBySeatId(Integer seatId);
}
