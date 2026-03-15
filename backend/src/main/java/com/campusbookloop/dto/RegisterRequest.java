package com.campusbookloop.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String phone;
    private String verifyCode;
    private String nickname;
    private String studentId;
    private String school;
}
