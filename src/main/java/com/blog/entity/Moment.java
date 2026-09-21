package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("moment")
public class Moment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String content;

    private String tag;

    private Integer likes;

    private LocalDateTime createTime;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private java.util.List<MomentMedia> mediaList;
}
