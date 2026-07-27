package com.liveplatform.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 用户服务Feign客户端
 */
@FeignClient("user-service")
public interface UserServiceClient {

    /**
     * 扣减用户余额 (Seata分布式事务)
     */
    @PutMapping("/api/user/balance/deduct")
    Boolean deductBalance(@RequestParam Long userId, @RequestParam BigDecimal amount);

    /**
     * 增加用户余额
     */
    @PutMapping("/api/user/balance/add")
    Boolean addBalance(@RequestParam Long userId, @RequestParam BigDecimal amount);
}
