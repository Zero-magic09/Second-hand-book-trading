package com.campusbookloop.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class WantedPostCreateRequest {
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    private String author;
    
    private String description;
    
    private String category;
    
    private BigDecimal maxPrice;
}
