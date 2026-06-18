package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article")
public class Article {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String summary;
    
    private String content;
    
    private String coverUrl;
    
    private Long categoryId;
    
    private Boolean isFeatured;
    
    private LocalDateTime createTime;

}
