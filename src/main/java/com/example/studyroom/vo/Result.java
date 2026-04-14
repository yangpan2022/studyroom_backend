package com.example.studyroom.vo;

/**
 * 统一 API 响应体
 * 
 * @param <T> 数据类型
 */
public class Result<T> {

    /** 状态码：200=成功，其他=失败 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 响应数据 */
    private T data;

    // ─── 私有构造 ───────────────────────────────────────────────────────────────

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ─── 静态工厂方法 ────────────────────────────────────────────────────────────

    /** 成功（带数据） */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /** 成功（无数据，如删除/更新） */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /** 失败 */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /** 失败（自定义状态码） */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    // ─── Getter / Setter ────────────────────────────────────────────────────────

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
