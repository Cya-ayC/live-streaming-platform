package com.liveplatform.userservice.controller;

import com.liveplatform.common.dto.ResponseDTO;
import com.liveplatform.userservice.dto.LoginRequest;
import com.liveplatform.userservice.dto.LoginResponse;
import com.liveplatform.userservice.dto.UserDTO;
import com.liveplatform.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseDTO<UserDTO> register(@RequestParam String username,
                                          @RequestParam String password,
                                          @RequestParam String email) {
        try {
            UserDTO user = userService.register(username, password, email);
            return ResponseDTO.success(user);
        } catch (Exception e) {
            log.error("[Register] Error: {}", e.getMessage());
            return ResponseDTO.error(400, e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseDTO<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return ResponseDTO.success(response);
        } catch (Exception e) {
            log.error("[Login] Error: {}", e.getMessage());
            return ResponseDTO.error(401, e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}")
    public ResponseDTO<UserDTO> getUser(@PathVariable Long userId) {
        try {
            UserDTO user = userService.getUserById(userId);
            return ResponseDTO.success(user);
        } catch (Exception e) {
            log.error("[GetUser] Error: {}", e.getMessage());
            return ResponseDTO.error(404, e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{userId}")
    public ResponseDTO<UserDTO> updateUser(@PathVariable Long userId,
                                           @RequestBody UserDTO dto) {
        try {
            UserDTO user = userService.updateUser(userId, dto);
            return ResponseDTO.success(user);
        } catch (Exception e) {
            log.error("[UpdateUser] Error: {}", e.getMessage());
            return ResponseDTO.error(400, e.getMessage());
        }
    }
}
