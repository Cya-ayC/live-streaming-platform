package com.liveplatform.liveservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 直播间实体类
 */
@Data
@Entity
@Table(name = "live_rooms")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveRoom implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String roomId; // 直播间唯一标识

    @Column(nullable = false)
    private Long anchorId; // 主播ID

    @Column(nullable = false, length = 200)
    private String title; // 直播间标题

    @Column(columnDefinition = "TEXT")
    private String description; // 直播间描述

    @Column(length = 500)
    private String coverUrl; // 直播间封面

    @Column
    private Long categoryId; // 分类ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LiveStatus status = LiveStatus.PREPARING; // 直播状态

    @Column
    private Integer viewerCount = 0; // 观看人数

    @Column
    private Long likeCount = 0L; // 点赞数

    @Column
    private Long shareCount = 0L; // 分享数

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private LocalDateTime startTime; // 开播时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private LocalDateTime endTime; // 下播时间

    @Column(length = 500)
    private String pushUrl; // 推流地址

    @Column(length = 500)
    private String pullUrl; // 拉流地址

    @Column
    private Boolean isPublic = true; // 是否公开

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime updateTime;

    @Column
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    /**
     * 直播状态枚举
     */
    public enum LiveStatus {
        PREPARING("准备中"),
        LIVE("直播中"),
        FINISHED("已结束");

        private final String desc;

        LiveStatus(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }
}
