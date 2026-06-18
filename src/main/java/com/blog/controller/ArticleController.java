package com.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.Result;
import com.blog.service.ArticleService;
import com.blog.vo.ArticleDTO;
import com.blog.vo.ArticleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 分页查询文章列表 (附带分类和标签)
     */
    @GetMapping("/list")
    public Result<Page<ArticleVO>> getArticleList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<ArticleVO> pageData = articleService.getArticlePage(current, size);
        return Result.success(pageData);
    }

    /**
     * [管理端] 发布文章
     */
    @PostMapping("/admin/save")
    public Result<String> saveArticle(@RequestBody ArticleDTO dto) {
        articleService.saveArticle(dto);
        return Result.success("发布成功");
    }

    /**
     * [管理端] 修改文章
     */
    @PutMapping("/admin/update")
    public Result<String> updateArticle(@RequestBody ArticleDTO dto) {
        articleService.updateArticle(dto);
        return Result.success("修改成功");
    }

    /**
     * [管理端] 删除文章
     */
    @DeleteMapping("/admin/{id}")
    public Result<String> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success("删除成功");
    }

}
