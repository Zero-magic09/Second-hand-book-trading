package com.campusbookloop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String orderNo;  // 订单号
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;  // 书籍
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;  // 买家
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;  // 卖家
    
    @Column(nullable = false)
    private BigDecimal price;  // 成交价格
    
    @Column(length = 200)
    private String address;  // 交易地址
    
    @Column(length = 500)
    private String remark;  // 备注
    
    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer status;  // 状态: 0-待付款 1-待发货 2-待收货 3-已完成 4-已取消
    
    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer paymentMethod;  // 支付方式: 0-线下支付 1-微信支付
    
    private LocalDateTime payTime;  // 支付时间
    
    private LocalDateTime deliveryTime;  // 发货时间
    
    private LocalDateTime receiveTime;  // 收货时间
    
    private LocalDateTime cancelTime;  // 取消时间
    
    @Column(length = 200)
    private String cancelReason;  // 取消原因
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // 生成订单号
        if (orderNo == null) {
            orderNo = "ORD" + System.currentTimeMillis() + (int)(Math.random() * 1000);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
