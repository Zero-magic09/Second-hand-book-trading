package com.campusbookloop.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class OrderCreateRequest {
    
    @NotNull(message = "书籍ID不能为空")
    private Long bookId;
    
    private String address;
    
    private String remark;
    
    private Integer paymentMethod;  // 0-线下支付 1-微信支付
}
