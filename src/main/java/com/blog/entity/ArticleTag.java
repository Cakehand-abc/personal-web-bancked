package com.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("article_tag")
public class ArticleTag {

    // 联合主键表，无需 @TableId
    private Long articleId;

    private Long tagId;

}
