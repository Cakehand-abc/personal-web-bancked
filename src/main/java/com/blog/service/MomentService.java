package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Moment;
import com.blog.vo.MomentDTO;

public interface MomentService extends IService<Moment> {

    /**
     * 后台发布新动态（级联保存多媒体文件）
     */
    void saveMoment(MomentDTO dto);

    /**
     * 后台删除动态（级联删除多媒体文件）
     */
    void deleteMoment(Long id);

    /**
     * 获取所有随笔动态（包含多媒体列表）
     */
    java.util.List<Moment> listWithMedia();

    /**
     * 点赞随笔动态
     */
    Integer likeMoment(Long id);

}
