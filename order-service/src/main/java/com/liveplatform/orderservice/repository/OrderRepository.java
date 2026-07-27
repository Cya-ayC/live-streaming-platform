package com.liveplatform.orderservice.repository;

import com.liveplatform.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * 订单数据仓库
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 根据订单号查询
     */
    Optional<Order> findByOrderId(String orderId);

    /**
     * 根据用户ID查询订单
     */
    List<Order> findByUserId(Long userId);

    /**
     * 根据订单状态查询
     */
    List<Order> findByStatus(Order.OrderStatus status);
}
