package com.liveplatform.liveservice.repository;

import com.liveplatform.liveservice.entity.LiveRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * 直播间数据仓库
 */
@Repository
public interface LiveRoomRepository extends JpaRepository<LiveRoom, Long> {

    /**
     * 根据直播间ID查询
     */
    Optional<LiveRoom> findByRoomId(String roomId);

    /**
     * 根据主播ID查询直播间
     */
    List<LiveRoom> findByAnchorId(Long anchorId);

    /**
     * 查询所有直播中的直播间
     */
    List<LiveRoom> findByStatus(LiveRoom.LiveStatus status);

    /**
     * 检查直播间是否存在
     */
    boolean existsByRoomId(String roomId);
}
