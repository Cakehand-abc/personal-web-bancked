package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("gallery_album")
public class GalleryAlbum {

    @TableId(type = IdType.INPUT)
    private String id;

    private String name;

    private String description;

    private String location;

    @TableField("album_date")
    @JsonProperty("date")
    private String date;

    private String cover;

    @TableField("raw_tags")
    private String rawTags;

    private Integer sortOrder;

    private LocalDateTime createTime;

    @TableField(exist = false)
    private List<String> tags;

    @TableField(exist = false)
    private List<GalleryPhoto> photos;
}
