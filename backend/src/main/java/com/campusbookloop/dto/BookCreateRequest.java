package com.campusbookloop.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;

@Data
public class BookCreateRequest {
    
    @NotBlank(message = "书名不能为空")
    private String title;
    
    private String author;
    
    private String publisher;
    
    private String isbn;
    
    @NotNull(message = "原价不能为空")
    @DecimalMin(value = "0.01", message = "原价必须大于0")
    private BigDecimal originalPrice;
    
    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.01", message = "售价必须大于0")
    private BigDecimal price;
    
    @NotBlank(message = "成色不能为空")
    private String condition;
    
    @NotBlank(message = "分类不能为空")
    private String category;
    
    private String description;
    
    private List<String> images;
}
