package com.liveplatform.liveservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 直播间创建请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLiveRoomRequest {

    /**
     * 直播间标题
     */
    private String title;

    /**
     * 直播间描述
     */
    private String description;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 是否公开
     */
    private Boolean isPublic = true;
}
