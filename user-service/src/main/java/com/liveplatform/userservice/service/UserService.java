package com.liveplatform.userservice.service;

import com.liveplatform.userservice.dto.LoginRequest;
import com.liveplatform.userservice.dto.LoginResponse;
import com.liveplatform.userservice.dto.UserDTO;
import com.liveplatform.userservice.entity.User;
import com.liveplatform.userservice.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * 用户业务服务
 */
@Service
@Slf4j
public class UserService {

    private static final String SECRET_KEY = "your-secret-key-change-it-in-production";
    private static final long EXPIRATION_TIME = 86400000; // 24小时

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册
     */
    @Transactional
    public UserDTO register(String username, String password, String email) {
        log.info("[User] Registering user: {}", username);

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        // 创建新用户
        User user = User.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .email(email)
            .nickName(username)
            .accountStatus(1)
            .build();

        user = userRepository.save(user);
        log.info("[User] User registered successfully: {}", username);

        return convertToDTO(user);
    }

    /**
     * 用户登录
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("[User] User login attempt: {}", request.getUsername());

        // 查询用户
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 检查账户状态
        if (user.getAccountStatus() == 0) {
            throw new RuntimeException("Account is disabled");
        }

        // 生成JWT Token
        String token = generateToken(user);

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        log.info("[User] User login successful: {}", request.getUsername());

        return LoginResponse.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .token(token)
            .expiresIn(EXPIRATION_TIME)
            .build();
    }

    /**
     * 获取用户信息
     */
    public UserDTO getUserById(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }
        return convertToDTO(userOpt.get());
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO dto) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        if (dto.getNickName() != null) {
            user.setNickName(dto.getNickName());
        }
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }

        user = userRepository.save(user);
        return convertToDTO(user);
    }

    /**
     * 生成JWT Token
     */
    private String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("username", user.getUsername())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes())
            .compact();
    }

    /**
     * 转换为DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
