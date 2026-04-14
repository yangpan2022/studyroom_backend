package com.example.studyroom.exception;

import com.example.studyroom.vo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 捕获所有 Controller 层抛出的异常，统一返回 Result 格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常（手动抛出的 RuntimeException）
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.error(500, "服务器内部错误：" + e.getMessage());
    }
}
