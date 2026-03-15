package com.campusbookloop.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Long id;
    private String orderNo;
    private BookDTO book;
    private UserDTO buyer;
    private UserDTO seller;
    private BigDecimal price;
    private String address;
    private String remark;
    private Integer status;
    private Integer paymentMethod;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime receiveTime;
    private LocalDateTime createdAt;
}
