package com.campusbookloop.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class VerificationRequest {
    
    @NotBlank(message = "学校名称不能为空")
    private String school;
    
    @NotBlank(message = "学号不能为空")
    private String studentId;
    
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    private String studentIdCardUrl;  // 学生证照片URL
}
