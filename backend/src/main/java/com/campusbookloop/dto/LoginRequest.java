package com.campusbookloop.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String code;  // 微信登录code
    private String phone;  // 手机号登录
    private String verifyCode;  // 验证码
}
