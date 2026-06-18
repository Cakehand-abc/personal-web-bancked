package com.blog.vo;

import com.blog.entity.Tag;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleVO {
    private Long id;
    private String title;
    private String summary;
    private String coverUrl;
    private Boolean isFeatured;
    private LocalDateTime createTime;

    // 关联查出的分类名称
    private String categoryName;
    // 关联查出的标签列表
    private List<Tag> tags;
}
