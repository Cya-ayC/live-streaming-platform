package com.liveplatform.liveservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 直播间DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRoomDTO {

    private Long id;
    private String roomId;
    private Long anchorId;
    private String title;
    private String description;
    private String coverUrl;
    private Long categoryId;
    private String status;
    private Integer viewerCount;
    private Long likeCount;
    private Long shareCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private String pushUrl;
    private String pullUrl;
    private Boolean isPublic;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
