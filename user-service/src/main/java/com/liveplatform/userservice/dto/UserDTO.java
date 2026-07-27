package com.liveplatform.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用户信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String nickName;
    private String avatarUrl;
    private String bio;
    private Integer gender;
    private Integer followCount;
    private Integer followerCount;
    private BigDecimal accountBalance;
    private Integer accountStatus;
}
