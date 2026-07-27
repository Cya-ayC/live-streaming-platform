package com.liveplatform.orderservice.controller;

import com.liveplatform.common.dto.ResponseDTO;
import com.liveplatform.orderservice.dto.OrderDTO;
import com.liveplatform.orderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public ResponseDTO<OrderDTO> createOrder(
        @RequestHeader("X-User-Id") Long userId,
        @RequestParam Long productId,
        @RequestParam String productName,
        @RequestParam Integer quantity,
        @RequestParam BigDecimal price) {
        try {
            OrderDTO order = orderService.createOrder(userId, productId, productName, quantity, price);
            return ResponseDTO.success(order);
        } catch (Exception e) {
            log.error("[CreateOrder] Error: {}", e.getMessage());
            return ResponseDTO.error(400, e.getMessage());
        }
    }

    /**
     * 订单支付
     */
    @PostMapping("/{orderId}/pay")
    public ResponseDTO<OrderDTO> payOrder(@PathVariable String orderId) {
        try {
            OrderDTO order = orderService.payOrder(orderId);
            return ResponseDTO.success(order);
        } catch (Exception e) {
            log.error("[PayOrder] Error: {}", e.getMessage());
            return ResponseDTO.error(400, e.getMessage());
        }
    }

    /**
     * 获取订单
     */
    @GetMapping("/{orderId}")
    public ResponseDTO<OrderDTO> getOrder(@PathVariable String orderId) {
        try {
            OrderDTO order = orderService.getOrder(orderId);
            return ResponseDTO.success(order);
        } catch (Exception e) {
            log.error("[GetOrder] Error: {}", e.getMessage());
            return ResponseDTO.error(404, e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseDTO<OrderDTO> cancelOrder(@PathVariable String orderId) {
        try {
            OrderDTO order = orderService.cancelOrder(orderId);
            return ResponseDTO.success(order);
        } catch (Exception e) {
            log.error("[CancelOrder] Error: {}", e.getMessage());
            return ResponseDTO.error(400, e.getMessage());
        }
    }
}
