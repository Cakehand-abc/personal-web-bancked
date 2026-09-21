package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gallery_photo")
public class GalleryPhoto {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String albumId;

    private String url;

    private String title;

    @JsonProperty("desc")
    private String description;

    private Integer sortOrder;

    private LocalDateTime createTime;
}
