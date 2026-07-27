package com.liveplatform.liveservice.controller;

import com.liveplatform.common.dto.ResponseDTO;
import com.liveplatform.liveservice.dto.CreateLiveRoomRequest;
import com.liveplatform.liveservice.dto.LiveRoomDTO;
import com.liveplatform.liveservice.service.LiveRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 直播间控制器
 */
@RestController
@RequestMapping("/api/live")
@Slf4j
public class LiveRoomController {

    @Autowired
    private LiveRoomService liveRoomService;

    /**
     * 创建直播间
     */
    @PostMapping("/room/create")
    public ResponseDTO<LiveRoomDTO> createLiveRoom(
        @RequestHeader("X-User-Id") Long anchorId,
        @RequestBody CreateLiveRoomRequest request) {
        try {
            LiveRoomDTO room = liveRoomService.createLiveRoom(anchorId, request);
            return ResponseDTO.success(room);
        } catch (Exception e) {
            log.error("[CreateLiveRoom] Error: {}", e.getMessage());
            return ResponseDTO.error(400, e.getMessage());
        }
    }

    /**
     * 获取直播间信息
     */
    @GetMapping("/room/{roomId}")
    public ResponseDTO<LiveRoomDTO> getLiveRoom(@PathVariable String roomId) {
        try {
            LiveRoomDTO room = liveRoomService.getLiveRoom(roomId);
            return ResponseDTO.success(room);
        } catch (Exception e) {
            log.error("[GetLiveRoom] Error: {}", e.getMessage());
            return ResponseDTO.error(404, e.getMessage());
        }
    }

    /**
     * 开始直播
     */
    @PostMapping("/room/{roomId}/start")
    public ResponseDTO<LiveRoomDTO> startLive(@PathVariable String roomId) {
        try {
            LiveRoomDTO room = liveRoomService.startLive(roomId);
            return ResponseDTO.success(room);
        } catch (Exception e) {
            log.error("[StartLive] Error: {}", e.getMessage());
            return ResponseDTO.error(400, e.getMessage());
        }
    }

    /**
     * 结束直播
     */
    @PostMapping("/room/{roomId}/end")
    public ResponseDTO<LiveRoomDTO> endLive(@PathVariable String roomId) {
        try {
            LiveRoomDTO room = liveRoomService.endLive(roomId);
            return ResponseDTO.success(room);
        } catch (Exception e) {
            log.error("[EndLive] Error: {}", e.getMessage());
            return ResponseDTO.error(400, e.getMessage());
        }
    }

    /**
     * 获取所有直播中的直播间
     */
    @GetMapping("/rooms/list")
    public ResponseDTO<List<LiveRoomDTO>> getLiveRooms() {
        try {
            List<LiveRoomDTO> rooms = liveRoomService.getLiveRooms();
            return ResponseDTO.success(rooms);
        } catch (Exception e) {
            log.error("[GetLiveRooms] Error: {}", e.getMessage());
            return ResponseDTO.error(500, e.getMessage());
        }
    }
}
