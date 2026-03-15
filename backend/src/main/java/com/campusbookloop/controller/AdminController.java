package com.campusbookloop.controller;

import com.campusbookloop.entity.*;
import com.campusbookloop.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final WantedPostRepository wantedPostRepository;
    private final FavoriteRepository favoriteRepository;
    private final ObjectMapper objectMapper;

    // 分类列表
    private static final List<String> CATEGORIES = Arrays.asList(
            "计算机", "经济管理", "机械工程", "外国语", "建筑设计", "通识教育", "考研资料");

    /**
     * 后台首页 - 数据概览
     */
    @GetMapping({ "", "/" })
    public String dashboard(Model model) {
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("bookCount", bookRepository.count());
        model.addAttribute("orderCount", orderRepository.count());
        model.addAttribute("wantedCount", wantedPostRepository.count());
        model.addAttribute("pendingVerifications", userRepository.findByVerificationStatus(1).size());

        // Prepare Chart Data (Last 7 Days)
        List<String> chartDates = new java.util.ArrayList<>();
        List<Long> chartData = new java.util.ArrayList<>();

        java.time.LocalDate today = java.time.LocalDate.now();
        List<Order> allOrders = orderRepository.findAll();

        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            String dateStr = date.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd"));
            chartDates.add(dateStr);

            long count = allOrders.stream()
                    .filter(o -> o.getCreatedAt().toLocalDate().equals(date))
                    .count();
            chartData.add(count);
        }

        model.addAttribute("chartDates", chartDates);
        model.addAttribute("chartData", chartData);

        return "admin/dashboard";
    }

    /**
     * 用户管理 - 列表和搜索
     */
    @GetMapping("/users")
    public String userList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        Page<User> users;
        if (keyword != null && !keyword.isEmpty()) {
            users = userRepository.searchByNickname(keyword,
                    PageRequest.of(page, size, Sort.by("createdAt").descending()));
        } else {
            users = userRepository.findAll(
                    PageRequest.of(page, size, Sort.by("createdAt").descending()));
        }
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "admin/users";
    }

    /**
     * 新增用户页面
     */
    @GetMapping("/users/add")
    public String addUserPage() {
        return "admin/user-add";
    }

    /**
     * 保存新用户
     */
    @PostMapping("/users/add")
    public String saveUser(@RequestParam String nickname,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String school,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String avatarUrl,
            @RequestParam(required = false) String studentIdCardUrl) {
        User user = new User();
        user.setOpenId("admin_created_" + System.currentTimeMillis());
        user.setNickname(nickname);
        user.setPhone(phone);
        user.setSchool(school);
        user.setStudentId(studentId);
        user.setAvatarUrl(avatarUrl != null && !avatarUrl.isEmpty() ? avatarUrl : "https://picsum.photos/100");
        if (studentIdCardUrl != null && !studentIdCardUrl.isEmpty()) {
            user.setStudentIdCardUrl(studentIdCardUrl);
        }
        user.setVerificationStatus(0);
        user.setRole(0);
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    /**
     * 编辑用户页面
     */
    @GetMapping("/users/{userId}/edit")
    public String editUserPage(@PathVariable Long userId, Model model) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        model.addAttribute("user", user);
        return "admin/user-edit";
    }

    /**
     * 更新用户
     */
    @PostMapping("/users/{userId}/edit")
    public String updateUser(@PathVariable Long userId,
            @RequestParam String nickname,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String school,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String avatarUrl,
            @RequestParam(required = false) String studentIdCardUrl,
            @RequestParam Integer verificationStatus) {
        userRepository.findById(userId).ifPresent(user -> {

            user.setNickname(nickname);
            user.setPhone(phone);
            user.setSchool(school);
            user.setStudentId(studentId);
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                user.setAvatarUrl(avatarUrl);
            }
            if (studentIdCardUrl != null && !studentIdCardUrl.isEmpty()) {
                user.setStudentIdCardUrl(studentIdCardUrl);
            }
            user.setVerificationStatus(verificationStatus);
            userRepository.save(user);
        });
        return "redirect:/admin/users";
    }

    /**
     * 删除用户
     */
    @PostMapping("/users/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
        try {
            // 1. 查找并删除该用户发布的所有书籍
            List<Book> userBooks = bookRepository.findAll().stream()
                    .filter(book -> book.getSeller() != null && book.getSeller().getId().equals(userId))
                    .toList();

            for (Book book : userBooks) {
                // 删除书籍相关的订单
                List<Order> bookOrders = orderRepository.findAll().stream()
                        .filter(order -> order.getBook() != null && order.getBook().getId().equals(book.getId()))
                        .toList();
                orderRepository.deleteAll(bookOrders);

                // 删除书籍
                bookRepository.delete(book);
            }

            // 2. 删除该用户作为买家的订单
            List<Order> buyerOrders = orderRepository.findAll().stream()
                    .filter(order -> order.getBuyer() != null && order.getBuyer().getId().equals(userId))
                    .toList();
            orderRepository.deleteAll(buyerOrders);

            // 3. 删除该用户发布的求购帖子（需要根据实际WantedPost结构调整）
            // List<WantedPost> userWantedPosts = wantedPostRepository.findAll().stream()
            // .filter(post -> post.getUser() != null &&
            // post.getUser().getId().equals(userId))
            // .toList();
            // wantedPostRepository.deleteAll(userWantedPosts);

            // 4. 最后删除用户
            userRepository.deleteById(userId);

        } catch (Exception e) {
            e.printStackTrace();
            // 可以添加错误处理逻辑
        }

        return "redirect:/admin/users";
    }

    /**
     * 书籍管理 - 列表和搜索
     */
    @GetMapping("/books")
    public String bookList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            Model model) {
        Page<Book> books;
        if (keyword != null && !keyword.isEmpty() && category != null && !category.isEmpty()) {
            books = bookRepository.searchByKeywordAndCategory(keyword, category,
                    PageRequest.of(page, size, Sort.by("createdAt").descending()));
        } else if (keyword != null && !keyword.isEmpty()) {
            books = bookRepository.searchByKeyword(keyword,
                    PageRequest.of(page, size, Sort.by("createdAt").descending()));
        } else if (category != null && !category.isEmpty()) {
            books = bookRepository.findByCategory(category,
                    PageRequest.of(page, size, Sort.by("createdAt").descending()));
        } else {
            books = bookRepository.findAll(
                    PageRequest.of(page, size, Sort.by("createdAt").descending()));
        }
        model.addAttribute("books", books);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("categories", CATEGORIES);
        return "admin/books";
    }

    /**
     * 新增书籍页面
     */
    @GetMapping("/books/add")
    public String addBookPage(Model model) {
        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("users", userRepository.findAll());
        return "admin/book-add";
    }

    /**
     * 保存新书籍
     */
    @PostMapping("/books/add")
    public String saveBook(@RequestParam String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String publisher,
            @RequestParam BigDecimal originalPrice,
            @RequestParam BigDecimal price,
            @RequestParam String bookCondition,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String imageUrl,
            @RequestParam Long sellerId) {

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPublisher(publisher);
        book.setOriginalPrice(originalPrice);
        book.setPrice(price);
        book.setCondition(bookCondition);
        book.setCategory(category);
        book.setDescription(description);
        book.setSeller(seller);
        book.setStatus(0); // 在售
        book.setViewCount(0);
        book.setFavoriteCount(0);

        // 设置图片
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                book.setImages(objectMapper.writeValueAsString(Arrays.asList(imageUrl)));
            } catch (JsonProcessingException e) {
                book.setImages("[]");
            }
        } else {
            book.setImages("[]");
        }

        bookRepository.save(book);
        return "redirect:/admin/books";
    }

    /**
     * 编辑书籍页面
     */
    @GetMapping("/books/{bookId}/edit")
    public String editBookPage(@PathVariable Long bookId, Model model) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));
        model.addAttribute("book", book);
        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("users", userRepository.findAll());

        // 解析图片URL
        String imageUrl = "";
        if (book.getImages() != null && !book.getImages().equals("[]")) {
            try {
                List<String> images = objectMapper.readValue(book.getImages(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                if (!images.isEmpty()) {
                    imageUrl = images.get(0);
                }
            } catch (JsonProcessingException e) {
                // ignore
            }
        }
        model.addAttribute("imageUrl", imageUrl);
        return "admin/book-edit";
    }

    /**
     * 更新书籍
     */
    @PostMapping("/books/{bookId}/edit")
    public String updateBook(@PathVariable Long bookId,
            @RequestParam String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String publisher,
            @RequestParam BigDecimal originalPrice,
            @RequestParam BigDecimal price,
            @RequestParam String bookCondition,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String imageUrl,
            @RequestParam Integer status) {

        bookRepository.findById(bookId).ifPresent(book -> {
            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setPublisher(publisher);
            book.setOriginalPrice(originalPrice);
            book.setPrice(price);
            book.setCondition(bookCondition);
            book.setCategory(category);
            book.setDescription(description);
            book.setStatus(status);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    book.setImages(objectMapper.writeValueAsString(Arrays.asList(imageUrl)));
                } catch (JsonProcessingException e) {
                    // ignore
                }
            }

            bookRepository.save(book);
        });
        return "redirect:/admin/books";
    }

    /**
     * 删除书籍
     */
    /**
     * 删除书籍
     */
    @PostMapping("/books/{bookId}/delete")
    public String deleteBook(@PathVariable Long bookId) {
        try {
            // 1. 删除相关订单
            List<Order> orders = orderRepository.findAll().stream()
                    .filter(order -> order.getBook() != null && order.getBook().getId().equals(bookId))
                    .toList();
            if (!orders.isEmpty()) {
                orderRepository.deleteAll(orders);
            }

            // 2. 删除相关收藏
            List<Favorite> favorites = favoriteRepository.findByBookId(bookId);
            if (!favorites.isEmpty()) {
                favoriteRepository.deleteAll(favorites);
            }

            // 3. 删除书籍
            bookRepository.deleteById(bookId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/books";
    }

    /**
     * 订单管理
     */
    @GetMapping("/orders")
    public String orderList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<Order> orders = orderRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        return "admin/orders";
    }

    /**
     * 认证审核
     */
    @GetMapping("/verifications")
    public String verificationList(@RequestParam(required = false) Integer status, Model model) {
        // 获取所有有认证记录的用户（状态 > 0）
        List<User> allVerifications = userRepository.findAll().stream()
                .filter(user -> user.getVerificationStatus() > 0)
                .filter(user -> status == null || user.getVerificationStatus().equals(status))
                .sorted((u1, u2) -> {
                    // 先按状态排序：待审核(1) > 已认证(2) > 认证失败(3)
                    if (!u1.getVerificationStatus().equals(u2.getVerificationStatus())) {
                        return u1.getVerificationStatus().compareTo(u2.getVerificationStatus());
                    }
                    // 状态相同时按ID倒序（最新的在前）
                    return u2.getId().compareTo(u1.getId());
                })
                .toList();

        model.addAttribute("allVerifications", allVerifications);
        model.addAttribute("currentStatus", status);
        model.addAttribute("pendingCount", userRepository.findAll().stream()
                .filter(u -> u.getVerificationStatus() == 1).count());
        model.addAttribute("approvedCount", userRepository.findAll().stream()
                .filter(u -> u.getVerificationStatus() == 2).count());
        model.addAttribute("rejectedCount", userRepository.findAll().stream()
                .filter(u -> u.getVerificationStatus() == 3).count());
        return "admin/verifications";
    }

    /**
     * 审核通过
     */
    @PostMapping("/verifications/{userId}/approve")
    public String approveVerification(@PathVariable Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setVerificationStatus(2);
            userRepository.save(user);
        });
        return "redirect:/admin/verifications";
    }

    /**
     * 审核拒绝
     */
    @PostMapping("/verifications/{userId}/reject")
    public String rejectVerification(@PathVariable Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setVerificationStatus(3);
            userRepository.save(user);
        });
        return "redirect:/admin/verifications";
    }

    /**
     * 求购管理
     */
    @GetMapping("/wanted")
    public String wantedList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        Page<WantedPost> posts;
        if (keyword != null && !keyword.isEmpty()) {
            posts = wantedPostRepository.searchWantedPosts(1, keyword, // Assuming 1 is active status
                    PageRequest.of(page, size, Sort.by("createdAt").descending()));
        } else {
            posts = wantedPostRepository.findAll(
                    PageRequest.of(page, size, Sort.by("createdAt").descending()));
        }

        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "admin/wanted";
    }

    /**
     * 删除求购
     */
    @PostMapping("/wanted/{id}/delete")
    public String deleteWantedPost(@PathVariable Long id) {
        wantedPostRepository.deleteById(id);
        return "redirect:/admin/wanted";
    }
}
