package com.liveplatform.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String orderId; // 订单号

    @Column(nullable = false)
    private Long userId; // 用户ID

    @Column(nullable = false)
    private Long productId; // 商品ID

    @Column(length = 200)
    private String productName; // 商品名称

    @Column(nullable = false)
    private Integer quantity = 1; // 数量

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // 单价

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount; // 总金额

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING; // 订单状态

    @Column(length = 50)
    private String paymentMethod; // 支付方式

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private LocalDateTime paymentTime; // 支付时间

    @Column(length = 500)
    private String remark; // 备注

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
     * 订单状态枚举
     */
    public enum OrderStatus {
        PENDING("待支付"),
        PAID("已支付"),
        DELIVERING("配送中"),
        COMPLETED("已完成"),
        CANCELLED("已取消"),
        REFUNDED("已退款");

        private final String desc;

        OrderStatus(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }
}
