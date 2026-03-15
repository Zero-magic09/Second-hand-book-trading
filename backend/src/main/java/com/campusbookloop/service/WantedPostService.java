package com.campusbookloop.service;

import com.campusbookloop.dto.*;
import com.campusbookloop.entity.User;
import com.campusbookloop.entity.WantedPost;
import com.campusbookloop.repository.UserRepository;
import com.campusbookloop.repository.WantedPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WantedPostService {
    
    private final WantedPostRepository wantedPostRepository;
    private final UserRepository userRepository;
    
    /**
     * 发布求购
     */
    @Transactional
    public WantedPostDTO createWantedPost(Long userId, WantedPostCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        WantedPost post = new WantedPost();
        BeanUtils.copyProperties(request, post);
        post.setUser(user);
        post.setStatus(0);  // 求购中
        
        return convertToDTO(wantedPostRepository.save(post));
    }
    
    /**
     * 获取求购列表
     */
    public Page<WantedPostDTO> getWantedPosts(Integer status, String category, String keyword, 
                                               int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WantedPost> posts;
        
        if (keyword != null && !keyword.isEmpty()) {
            posts = wantedPostRepository.searchWantedPosts(status != null ? status : 0, keyword, pageable);
        } else if (category != null && !category.isEmpty()) {
            posts = wantedPostRepository.findByCategory(category, pageable);
        } else {
            posts = wantedPostRepository.findByStatus(status != null ? status : 0, pageable);
        }
        
        return posts.map(this::convertToDTO);
    }
    
    /**
     * 获取用户求购列表
     */
    public Page<WantedPostDTO> getUserWantedPosts(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return wantedPostRepository.findByUserId(userId, pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * 获取求购详情
     */
    public WantedPostDTO getWantedPostById(Long postId) {
        WantedPost post = wantedPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("求购不存在"));
        return convertToDTO(post);
    }
    
    /**
     * 更新求购状态
     */
    @Transactional
    public WantedPostDTO updateWantedPostStatus(Long postId, Long userId, Integer status) {
        WantedPost post = wantedPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("求购不存在"));
        
        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此求购");
        }
        
        post.setStatus(status);
        return convertToDTO(wantedPostRepository.save(post));
    }
    
    /**
     * 删除求购
     */
    @Transactional
    public void deleteWantedPost(Long postId, Long userId) {
        WantedPost post = wantedPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("求购不存在"));
        
        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此求购");
        }
        
        post.setStatus(2);  // 已关闭
        wantedPostRepository.save(post);
    }
    
    private WantedPostDTO convertToDTO(WantedPost post) {
        WantedPostDTO dto = new WantedPostDTO();
        BeanUtils.copyProperties(post, dto);
        dto.setUserId(post.getUser().getId());
        dto.setUserNickname(post.getUser().getNickname());
        dto.setUserAvatar(post.getUser().getAvatarUrl());
        return dto;
    }
}
