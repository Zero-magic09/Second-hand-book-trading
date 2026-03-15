package com.campusbookloop.service;

import com.campusbookloop.dto.*;
import com.campusbookloop.entity.Book;
import com.campusbookloop.entity.Order;
import com.campusbookloop.entity.User;
import com.campusbookloop.repository.BookRepository;
import com.campusbookloop.repository.OrderRepository;
import com.campusbookloop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * 创建订单
     */
    @Transactional
    public OrderDTO createOrder(Long buyerId, OrderCreateRequest request) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("书籍不存在"));

        if (book.getStatus() != 0) {
            throw new RuntimeException("书籍已售出或已下架");
        }

        if (book.getSeller().getId().equals(buyerId)) {
            throw new RuntimeException("不能购买自己发布的书籍");
        }

        Order order = new Order();
        order.setBook(book);
        order.setBuyer(buyer);
        order.setSeller(book.getSeller());
        order.setPrice(book.getPrice());
        order.setAddress(request.getAddress());
        order.setRemark(request.getRemark());
        order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : 0);
        order.setStatus(0); // 待付款

        // 更新书籍状态为已预订
        book.setStatus(1);
        bookRepository.save(book);

        // 确保订单状态为待付款(0)
        order.setStatus(0);
        return convertToDTO(orderRepository.save(order));
    }

    /**
     * 获取订单详情
     */
    public OrderDTO getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getBuyer().getId().equals(userId) && !order.getSeller().getId().equals(userId)) {
            throw new RuntimeException("无权查看此订单");
        }

        return convertToDTO(order);
    }

    /**
     * 获取买家订单列表
     */
    public Page<OrderDTO> getBuyerOrders(Long buyerId, Integer status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders;

        if (status != null) {
            orders = orderRepository.findByBuyerIdAndStatus(buyerId, status, pageable);
        } else {
            orders = orderRepository.findByBuyerId(buyerId, pageable);
        }

        return orders.map(this::convertToDTO);
    }

    /**
     * 获取卖家订单列表
     */
    public Page<OrderDTO> getSellerOrders(Long sellerId, Integer status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders;

        if (status != null) {
            orders = orderRepository.findBySellerIdAndStatus(sellerId, status, pageable);
        } else {
            orders = orderRepository.findBySellerId(sellerId, pageable);
        }

        return orders.map(this::convertToDTO);
    }

    /**
     * 支付订单
     */
    @Transactional
    public OrderDTO payOrder(Long orderId, Long buyerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getBuyer().getId().equals(buyerId)) {
            throw new RuntimeException("无权操作此订单");
        }

        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态异常");
        }

        order.setStatus(1); // 待发货
        order.setPayTime(LocalDateTime.now());

        return convertToDTO(orderRepository.save(order));
    }

    /**
     * 发货
     */
    @Transactional
    public OrderDTO deliverOrder(Long orderId, Long sellerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("无权操作此订单");
        }

        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态异常");
        }

        order.setStatus(2); // 待收货
        order.setDeliveryTime(LocalDateTime.now());

        return convertToDTO(orderRepository.save(order));
    }

    /**
     * 确认收货
     */
    @Transactional
    public OrderDTO confirmReceive(Long orderId, Long buyerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getBuyer().getId().equals(buyerId)) {
            throw new RuntimeException("无权操作此订单");
        }

        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态异常");
        }

        order.setStatus(3); // 已完成
        order.setReceiveTime(LocalDateTime.now());

        // 更新书籍状态为已售出
        Book book = order.getBook();
        book.setStatus(2);
        bookRepository.save(book);

        return convertToDTO(orderRepository.save(order));
    }

    /**
     * 取消订单
     */
    @Transactional
    public OrderDTO cancelOrder(Long orderId, Long userId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getBuyer().getId().equals(userId) && !order.getSeller().getId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }

        if (order.getStatus() >= 3) {
            throw new RuntimeException("订单已完成或已取消，无法取消");
        }

        order.setStatus(4); // 已取消
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(reason);

        // 恢复书籍状态为在售
        Book book = order.getBook();
        book.setStatus(0);
        bookRepository.save(book);

        return convertToDTO(orderRepository.save(order));
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);

        // 简化书籍信息
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(order.getBook().getId());
        bookDTO.setTitle(order.getBook().getTitle());
        bookDTO.setPrice(order.getBook().getPrice());

        // 处理图片
        String imagesJson = order.getBook().getImages();
        if (imagesJson != null && !imagesJson.isEmpty()) {
            try {
                // 简单的JSON解析，假设是 ["url1", "url2"] 格式
                if (imagesJson.startsWith("[\"") && imagesJson.endsWith("\"]")) {
                    String content = imagesJson.substring(2, imagesJson.length() - 2);
                    String[] urls = content.split("\",\"");
                    bookDTO.setImages(java.util.Arrays.asList(urls));
                }
            } catch (Exception e) {
                // 解析失败忽略
            }
        }
        dto.setBook(bookDTO);

        // 简化买家信息
        UserDTO buyerDTO = new UserDTO();
        buyerDTO.setId(order.getBuyer().getId());
        buyerDTO.setNickname(order.getBuyer().getNickname());
        buyerDTO.setAvatarUrl(order.getBuyer().getAvatarUrl());
        dto.setBuyer(buyerDTO);

        // 简化卖家信息
        UserDTO sellerDTO = new UserDTO();
        sellerDTO.setId(order.getSeller().getId());
        sellerDTO.setNickname(order.getSeller().getNickname());
        sellerDTO.setAvatarUrl(order.getSeller().getAvatarUrl());
        dto.setSeller(sellerDTO);

        return dto;
    }
}
