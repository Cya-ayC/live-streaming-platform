package com.liveplatform.orderservice.service;

import com.liveplatform.orderservice.dto.OrderDTO;
import com.liveplatform.orderservice.entity.Order;
import com.liveplatform.orderservice.feign.UserServiceClient;
import com.liveplatform.orderservice.repository.OrderRepository;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 订单服务 - 包含Seata分布式事务
 */
@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 创建订单 - 使用Seata分布式事务
     * 流程: 创建订单 -> 调用支付服务 -> 扣减用户余额
     */
    @GlobalTransactional(name = "createOrder", rollbackFor = Exception.class)
    @Transactional
    public OrderDTO createOrder(Long userId, Long productId, String productName, 
                                Integer quantity, BigDecimal price) {
        log.info("[Order] Creating order for user: {}, product: {}", userId, productId);

        // 1. 生成订单号
        String orderId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(quantity));

        // 2. 创建订单
        Order order = Order.builder()
            .orderId(orderId)
            .userId(userId)
            .productId(productId)
            .productName(productName)
            .quantity(quantity)
            .price(price)
            .totalAmount(totalAmount)
            .status(Order.OrderStatus.PENDING)
            .build();

        order = orderRepository.save(order);
        log.info("[Order] Order created: {}", orderId);

        // 3. 发送事件到消息队列
        rabbitTemplate.convertAndSend("order.events.ex", "order.created", order);

        return convertToDTO(order);
    }

    /**
     * 订单支付 - Seata分布式事务示例
     * 这里演示跨服务调用时的分布式事务协调
     */
    @GlobalTransactional(name = "payOrder", rollbackFor = Exception.class)
    @Transactional
    public OrderDTO payOrder(String orderId) {
        log.info("[Order] Processing payment for order: {}", orderId);

        // 1. 查询订单
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            throw new RuntimeException("Order not found: " + orderId);
        }

        Order order = orderOpt.get();
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Order status is not PENDING");
        }

        // 2. 调用支付服务扣减用户余额 (Seata会跟踪此调用)
        try {
            Boolean result = userServiceClient.deductBalance(order.getUserId(), order.getTotalAmount());
            if (!result) {
                throw new RuntimeException("Failed to deduct balance");
            }
        } catch (Exception e) {
            log.error("[Order] Payment failed: {}", e.getMessage());
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }

        // 3. 更新订单状态
        order.setStatus(Order.OrderStatus.PAID);
        order.setPaymentTime(LocalDateTime.now());
        order = orderRepository.save(order);

        log.info("[Order] Order paid successfully: {}", orderId);

        // 4. 发送支付成功事件
        rabbitTemplate.convertAndSend("order.events.ex", "order.paid", order);

        return convertToDTO(order);
    }

    /**
     * 获取订单详情
     */
    public OrderDTO getOrder(String orderId) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            throw new RuntimeException("Order not found");
        }
        return convertToDTO(orderOpt.get());
    }

    /**
     * 取消订单
     */
    @Transactional
    public OrderDTO cancelOrder(String orderId) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            throw new RuntimeException("Order not found");
        }

        Order order = orderOpt.get();
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Only pending orders can be cancelled");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        // 发送取消事件
        rabbitTemplate.convertAndSend("order.events.ex", "order.cancelled", order);

        return convertToDTO(order);
    }

    /**
     * 转换为DTO
     */
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);
        dto.setStatus(order.getStatus().name());
        return dto;
    }
}
