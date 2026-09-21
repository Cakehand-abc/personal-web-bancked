package com.blog.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentDTO {
    
    private Long id;
    
    private String content; // 动态的文字内容
    
    private String tag; // 动态标签分类，如：生活随笔、技术沉思
    
    private Integer likes; // 点赞数
    
    private LocalDateTime createTime; // 允许前端自定义伪造时间
    
    // 动态附带的无限混合媒体（图片/视频）
    private List<MomentMediaDTO> mediaList;

}
