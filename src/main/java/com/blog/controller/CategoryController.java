package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Category;
import com.blog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * [前台/管理端] 查询所有分类列表
     */
    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        return Result.success(categoryService.list());
    }

    /**
     * [管理端] 新增分类
     */
    @PostMapping("/admin/save")
    public Result<String> saveCategory(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success("新增分类成功");
    }

    /**
     * [管理端] 修改分类
     */
    @PutMapping("/admin/update")
    public Result<String> updateCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success("修改分类成功");
    }

    /**
     * [管理端] 删除分类
     */
    @DeleteMapping("/admin/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        // 注意：实际项目中通常需要判断该分类下是否有文章，如果有则禁止删除或提示用户
        categoryService.removeById(id);
        return Result.success("删除分类成功");
    }
}
