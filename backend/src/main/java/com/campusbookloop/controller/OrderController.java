package com.campusbookloop.controller;

import com.campusbookloop.dto.*;
import com.campusbookloop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public ApiResponse<OrderDTO> createOrder(@RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody OrderCreateRequest request) {
        try {
            OrderDTO order = orderService.createOrder(userId, request);
            return ApiResponse.success(order);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ApiResponse<OrderDTO> getOrderById(@PathVariable Long orderId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            OrderDTO order = orderService.getOrderById(orderId, userId);
            return ApiResponse.success(order);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取我的购买订单
     */
    @GetMapping("/buy")
    public ApiResponse<Page<OrderDTO>> getBuyerOrders(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<OrderDTO> orders = orderService.getBuyerOrders(userId, status, page, size);
            return ApiResponse.success(orders);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取我的出售订单
     */
    @GetMapping("/sell")
    public ApiResponse<Page<OrderDTO>> getSellerOrders(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<OrderDTO> orders = orderService.getSellerOrders(userId, status, page, size);
            return ApiResponse.success(orders);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 支付订单
     */
    @PostMapping("/{orderId}/pay")
    public ApiResponse<OrderDTO> payOrder(@PathVariable Long orderId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            OrderDTO order = orderService.payOrder(orderId, userId);
            return ApiResponse.success(order);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 发货
     */
    @PostMapping("/{orderId}/deliver")
    public ApiResponse<OrderDTO> deliverOrder(@PathVariable Long orderId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            OrderDTO order = orderService.deliverOrder(orderId, userId);
            return ApiResponse.success(order);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 确认收货
     */
    @PostMapping("/{orderId}/receive")
    public ApiResponse<OrderDTO> confirmReceive(@PathVariable Long orderId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            OrderDTO order = orderService.confirmReceive(orderId, userId);
            return ApiResponse.success(order);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    public ApiResponse<OrderDTO> cancelOrder(@PathVariable Long orderId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) String reason) {
        try {
            OrderDTO order = orderService.cancelOrder(orderId, userId, reason);
            return ApiResponse.success(order);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
