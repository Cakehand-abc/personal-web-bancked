package com.blog.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleDTO {
    
    private Long id; // 修改文章时需要传ID
    
    private String title;
    
    private String summary;
    
    private String content; // 富文本正文
    
    private String coverUrl;
    
    private Long categoryId;
    
    private Boolean isFeatured;
    
    private LocalDateTime createTime; // 允许前端自定义伪造时间
    
    private List<String> tags; // 标签名称列表，后端自动处理新建或绑定

}
