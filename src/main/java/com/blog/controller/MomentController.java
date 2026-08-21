package com.blog.controller;

import com.blog.common.Result;
import com.blog.service.MomentService;
import com.blog.vo.MomentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moments")
public class MomentController {

    @Autowired
    private MomentService momentService;

    /**
     * [前台/管理端] 获取所有随笔动态列表
     */
    @GetMapping("/list")
    public Result<java.util.List<com.blog.entity.Moment>> getMomentList() {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.blog.entity.Moment> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.orderByDesc(com.blog.entity.Moment::getCreateTime);
        return Result.success(momentService.list(wrapper));
    }

    /**
     * [管理端] 发布动态（支持无限混排多媒体）
     */
    @PostMapping("/admin/save")
    public Result<String> saveMoment(@RequestBody MomentDTO dto) {
        momentService.saveMoment(dto);
        return Result.success("动态发布成功");
    }

    /**
     * [管理端] 删除动态
     */
    @DeleteMapping("/admin/{id}")
    public Result<String> deleteMoment(@PathVariable Long id) {
        momentService.deleteMoment(id);
        return Result.success("动态删除成功");
    }
}
