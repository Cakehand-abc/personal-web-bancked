package com.blog.vo;

import lombok.Data;

@Data
public class MomentMediaDTO {
    private String mediaUrl;
    // 1: 图片, 2: 视频
    private Integer mediaType;
    private Integer sortOrder;
}
