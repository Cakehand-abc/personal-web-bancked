package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("site_setting")
public class SiteSetting {

    @TableId(type = IdType.INPUT)
    private Long id; // 固定为 1

    private String introMediaType; // "video" 或 "image"
    
    private String introMediaUrl; // 上传后的媒体链接

    private String siteName; // 博客名称

    private String siteSignature; // 站长签名

    private String recordNumber; // 备案号
}
