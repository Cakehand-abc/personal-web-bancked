package com.blog.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentDTO {
    
    private Long id;
    
    private String content; // 动态的文字内容
    
    private LocalDateTime createTime; // 允许前端自定义伪造时间
    
    // 动态附带的无限混合媒体（图片/视频）
    private List<MomentMediaDTO> mediaList;

}
