package com.liveplatform.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 成功响应
     */
    public static <T> ResponseDTO<T> success(T data) {
        return ResponseDTO.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 成功响应
     */
    public static <T> ResponseDTO<T> success() {
        return success(null);
    }

    /**
     * 错误响应
     */
    public static <T> ResponseDTO<T> error(Integer code, String message) {
        return ResponseDTO.<T>builder()
                .code(code)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 参数错误
     */
    public static <T> ResponseDTO<T> paramError(String message) {
        return error(400, message);
    }

    /**
     * 系统错误
     */
    public static <T> ResponseDTO<T> systemError() {
        return error(500, "System error");
    }
}
