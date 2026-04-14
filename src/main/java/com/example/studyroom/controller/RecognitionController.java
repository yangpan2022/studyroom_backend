package com.example.studyroom.controller;

import com.example.studyroom.dto.RecognitionUploadDTO;
import com.example.studyroom.po.RecognitionRecord;
import com.example.studyroom.service.RecognitionService;
import com.example.studyroom.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 识别模块接口 Controller
 * 基础路径：/recognition
 */
@RestController
@RequestMapping("/recognition")
public class RecognitionController {

    @Autowired
    private RecognitionService recognitionService;

    /**
     * AI 模块上传识别结果
     * POST /recognition/upload
     */
    @PostMapping("/upload")
    public Result<RecognitionRecord> upload(@RequestBody @Valid RecognitionUploadDTO uploadDTO) {
        RecognitionRecord record = new RecognitionRecord();
        record.setSeatId(uploadDTO.getSeatId());
        record.setOccupied(uploadDTO.getOccupied());
        record.setConfidence(uploadDTO.getConfidence());
        record.setDetectTime(uploadDTO.getDetectTime());
        return Result.success(recognitionService.uploadRecord(record));
    }

    /**
     * 查询识别记录
     * GET /recognition/records
     * 可接受查询参数：?seatId=1
     */
    @GetMapping("/records")
    public Result<List<RecognitionRecord>> getRecords(
            @RequestParam(required = false) Integer seatId) {
        if (seatId != null) {
            return Result.success(recognitionService.getRecordsBySeatId(seatId));
        }
        return Result.success(recognitionService.getAllRecords());
    }
}
