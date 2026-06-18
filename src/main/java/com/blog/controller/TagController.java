package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Tag;
import com.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * [前台/管理端] 查询所有标签列表
     */
    @GetMapping("/list")
    public Result<List<Tag>> getTagList() {
        return Result.success(tagService.list());
    }

    /**
     * [管理端] 新增标签
     */
    @PostMapping("/admin/save")
    public Result<String> saveTag(@RequestBody Tag tag) {
        tagService.save(tag);
        return Result.success("新增标签成功");
    }

    /**
     * [管理端] 修改标签
     */
    @PutMapping("/admin/update")
    public Result<String> updateTag(@RequestBody Tag tag) {
        tagService.updateById(tag);
        return Result.success("修改标签成功");
    }

    /**
     * [管理端] 删除标签
     */
    @DeleteMapping("/admin/{id}")
    public Result<String> deleteTag(@PathVariable Long id) {
        tagService.removeById(id);
        return Result.success("删除标签成功");
    }
}
