package com.liveplatform.liveservice.service;

import com.liveplatform.liveservice.dto.CreateLiveRoomRequest;
import com.liveplatform.liveservice.dto.LiveRoomDTO;
import com.liveplatform.liveservice.entity.LiveRoom;
import com.liveplatform.liveservice.repository.LiveRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 直播服务
 */
@Service
@Slf4j
public class LiveRoomService {

    @Autowired
    private LiveRoomRepository liveRoomRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String LIVE_ROOM_CACHE_PREFIX = "live:room:";
    private static final String LIVE_ROOMS_LIVE_KEY = "live:rooms:live";
    private static final long CACHE_TIMEOUT = 30; // 30分钟

    /**
     * 创建直播间
     */
    @Transactional
    public LiveRoomDTO createLiveRoom(Long anchorId, CreateLiveRoomRequest request) {
        log.info("[Live] Creating live room for anchor: {}", anchorId);

        // 生成直播间ID
        String roomId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);

        LiveRoom room = LiveRoom.builder()
            .roomId(roomId)
            .anchorId(anchorId)
            .title(request.getTitle())
            .description(request.getDescription())
            .categoryId(request.getCategoryId())
            .isPublic(request.getIsPublic())
            .status(LiveRoom.LiveStatus.PREPARING)
            .viewerCount(0)
            .likeCount(0L)
            .shareCount(0L)
            .build();

        room = liveRoomRepository.save(room);

        // 缓存直播间信息
        String cacheKey = LIVE_ROOM_CACHE_PREFIX + roomId;
        redisTemplate.opsForValue().set(cacheKey, room, CACHE_TIMEOUT, TimeUnit.MINUTES);

        // 发送事件到消息队列
        rabbitTemplate.convertAndSend("live.events.ex", "live.room.created", room);

        log.info("[Live] Live room created successfully: {}", roomId);
        return convertToDTO(room);
    }

    /**
     * 获取直播间信息
     */
    public LiveRoomDTO getLiveRoom(String roomId) {
        log.debug("[Live] Getting live room: {}", roomId);

        // 先查缓存
        String cacheKey = LIVE_ROOM_CACHE_PREFIX + roomId;
        LiveRoom cached = (LiveRoom) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("[Live] Live room found in cache: {}", roomId);
            return convertToDTO(cached);
        }

        // 再查数据库
        Optional<LiveRoom> roomOpt = liveRoomRepository.findByRoomId(roomId);
        if (!roomOpt.isPresent()) {
            throw new RuntimeException("Live room not found: " + roomId);
        }

        LiveRoom room = roomOpt.get();
        // 缓存
        redisTemplate.opsForValue().set(cacheKey, room, CACHE_TIMEOUT, TimeUnit.MINUTES);

        return convertToDTO(room);
    }

    /**
     * 开始直播
     */
    @Transactional
    public LiveRoomDTO startLive(String roomId) {
        log.info("[Live] Starting live room: {}", roomId);

        Optional<LiveRoom> roomOpt = liveRoomRepository.findByRoomId(roomId);
        if (!roomOpt.isPresent()) {
            throw new RuntimeException("Live room not found");
        }

        LiveRoom room = roomOpt.get();
        if (room.getStatus() != LiveRoom.LiveStatus.PREPARING) {
            throw new RuntimeException("Live room status is invalid");
        }

        room.setStatus(LiveRoom.LiveStatus.LIVE);
        room.setStartTime(LocalDateTime.now());
        room = liveRoomRepository.save(room);

        // 更新缓存
        String cacheKey = LIVE_ROOM_CACHE_PREFIX + roomId;
        redisTemplate.opsForValue().set(cacheKey, room, CACHE_TIMEOUT, TimeUnit.MINUTES);
        redisTemplate.opsForList().rightPush(LIVE_ROOMS_LIVE_KEY, roomId);

        // 发送事件
        rabbitTemplate.convertAndSend("live.events.ex", "live.room.started", room);

        log.info("[Live] Live room started: {}", roomId);
        return convertToDTO(room);
    }

    /**
     * 结束直播
     */
    @Transactional
    public LiveRoomDTO endLive(String roomId) {
        log.info("[Live] Ending live room: {}", roomId);

        Optional<LiveRoom> roomOpt = liveRoomRepository.findByRoomId(roomId);
        if (!roomOpt.isPresent()) {
            throw new RuntimeException("Live room not found");
        }

        LiveRoom room = roomOpt.get();
        if (room.getStatus() != LiveRoom.LiveStatus.LIVE) {
            throw new RuntimeException("Live room is not live");
        }

        room.setStatus(LiveRoom.LiveStatus.FINISHED);
        room.setEndTime(LocalDateTime.now());
        room = liveRoomRepository.save(room);

        // 更新缓存
        String cacheKey = LIVE_ROOM_CACHE_PREFIX + roomId;
        redisTemplate.opsForValue().set(cacheKey, room, CACHE_TIMEOUT, TimeUnit.MINUTES);
        redisTemplate.opsForList().remove(LIVE_ROOMS_LIVE_KEY, 0, roomId);

        // 发送事件
        rabbitTemplate.convertAndSend("live.events.ex", "live.room.ended", room);

        log.info("[Live] Live room ended: {}", roomId);
        return convertToDTO(room);
    }

    /**
     * 获取所有直播中的直播间
     */
    public List<LiveRoomDTO> getLiveRooms() {
        List<LiveRoom> rooms = liveRoomRepository.findByStatus(LiveRoom.LiveStatus.LIVE);
        return rooms.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * 更新观看人数
     */
    @Transactional
    public void updateViewerCount(String roomId, Integer count) {
        Optional<LiveRoom> roomOpt = liveRoomRepository.findByRoomId(roomId);
        if (roomOpt.isPresent()) {
            LiveRoom room = roomOpt.get();
            room.setViewerCount(count);
            liveRoomRepository.save(room);
            // 更新缓存
            redisTemplate.opsForValue().set(LIVE_ROOM_CACHE_PREFIX + roomId, room, CACHE_TIMEOUT, TimeUnit.MINUTES);
        }
    }

    /**
     * 转换为DTO
     */
    private LiveRoomDTO convertToDTO(LiveRoom room) {
        LiveRoomDTO dto = new LiveRoomDTO();
        BeanUtils.copyProperties(room, dto);
        dto.setStatus(room.getStatus().name());
        return dto;
    }
}
