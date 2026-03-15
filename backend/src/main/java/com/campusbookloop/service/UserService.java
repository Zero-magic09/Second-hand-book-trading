package com.campusbookloop.service;

import com.campusbookloop.dto.*;
import com.campusbookloop.entity.User;
import com.campusbookloop.repository.UserRepository;
import com.campusbookloop.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final com.campusbookloop.repository.BookRepository bookRepository;
    private final com.campusbookloop.repository.FavoriteRepository favoriteRepository;
    private final JwtUtil jwtUtil;

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);

        // 填充统计数据
        if (user.getId() != null) {
            dto.setSellingCount(bookRepository.countBySellerIdAndStatus(user.getId(), 0)); // 0: 在售
            dto.setSoldCount(bookRepository.countBySellerIdAndStatus(user.getId(), 2)); // 2: 已售
            dto.setFavoriteCount(favoriteRepository.countByUserId(user.getId()));
        } else {
            dto.setSellingCount(0L);
            dto.setSoldCount(0L);
            dto.setFavoriteCount(0L);
        }

        return dto;
    }

    /**
     * 微信登录
     */
    @Transactional
    public LoginResponse wxLogin(String code) {
        logger.info("开始微信登录，code: {}", code);
        // TODO: 调用微信接口获取openId
        // 这里简化处理，直接用code作为openId
        String openId = "wx_" + code;

        User user = userRepository.findByOpenId(openId)
                .orElseGet(() -> {
                    logger.info("创建新用户，openId: {}", openId);
                    User newUser = new User();
                    newUser.setOpenId(openId);
                    newUser.setNickname("微信用户" + System.currentTimeMillis() % 10000);
                    newUser.setVerificationStatus(0);
                    newUser.setRole(0);
                    newUser.setStatus(1);
                    return userRepository.save(newUser);
                });

        String token = jwtUtil.generateToken(user.getId());
        logger.info("用户登录成功，userId: {}, token生成成功", user.getId());
        return new LoginResponse(token, convertToDTO(user));
    }

    /**
     * 用户注册
     */
    @Transactional
    public LoginResponse register(String phone, String verifyCode, String nickname, String school, String studentId) {
        logger.info("开始用户注册，phone: {}", phone);
        // TODO: 验证验证码

        if (userRepository.findByPhone(phone).isPresent()) {
            throw new RuntimeException("该手机号已注册，请直接登录");
        }

        User newUser = new User();
        newUser.setPhone(phone);
        // 如果有昵称则设置，否则默认
        newUser.setNickname(nickname != null && !nickname.isEmpty()
                ? nickname
                : "用户" + phone.substring(phone.length() - 4));

        if (school != null && !school.isEmpty()) {
            newUser.setSchool(school);
        }

        if (studentId != null && !studentId.isEmpty()) {
            newUser.setStudentId(studentId);
        }

        newUser.setVerificationStatus(0);
        newUser.setRole(0);
        newUser.setStatus(1);
        User savedUser = userRepository.save(newUser);

        String token = jwtUtil.generateToken(savedUser.getId());
        logger.info("用户注册成功，userId: {}, token生成成功", savedUser.getId());
        return new LoginResponse(token, convertToDTO(savedUser));
    }

    /**
     * 手机号登录
     */
    @Transactional
    public LoginResponse phoneLogin(String phone, String verifyCode) {
        logger.info("开始手机号登录，phone: {}", phone);
        // TODO: 验证验证码
        User user = userRepository.findByPhone(phone)
                .orElseGet(() -> {
                    logger.info("创建新用户，phone: {}", phone);
                    User newUser = new User();
                    newUser.setPhone(phone);
                    newUser.setNickname("用户" + phone.substring(phone.length() - 4));
                    newUser.setVerificationStatus(0);
                    newUser.setRole(0);
                    newUser.setStatus(1);
                    return userRepository.save(newUser);
                });

        String token = jwtUtil.generateToken(user.getId());
        logger.info("用户登录成功，userId: {}, token生成成功", user.getId());
        return new LoginResponse(token, convertToDTO(user));
    }

    /**
     * 获取用户信息
     */
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToDTO(user);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (userDTO.getNickname() != null) {
            user.setNickname(userDTO.getNickname());
        }
        if (userDTO.getAvatarUrl() != null) {
            user.setAvatarUrl(userDTO.getAvatarUrl());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }

        return convertToDTO(userRepository.save(user));
    }

    /**
     * 提交学生认证
     */
    @Transactional
    public UserDTO submitVerification(Long userId, VerificationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setSchool(request.getSchool());
        user.setStudentId(request.getStudentId());
        user.setRealName(request.getRealName());
        user.setStudentIdCardUrl(request.getStudentIdCardUrl());
        user.setVerificationStatus(1); // 待审核

        return convertToDTO(userRepository.save(user));
    }

    /**
     * 审核学生认证
     */
    @Transactional
    public UserDTO auditVerification(Long userId, boolean approved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setVerificationStatus(approved ? 2 : 3); // 2-已认证 3-认证失败
        return convertToDTO(userRepository.save(user));
    }

    /**
     * 获取待审核认证列表
     */
    public List<UserDTO> getPendingVerifications() {
        return userRepository.findByVerificationStatus(1).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
