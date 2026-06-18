package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("moment_media")
public class MomentMedia {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long momentId;

    private String mediaUrl;

    // 1图片, 2视频
    private Integer mediaType;

    private Integer sortOrder;

}
