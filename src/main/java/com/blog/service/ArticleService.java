package com.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Article;
import com.blog.vo.ArticleDTO;
import com.blog.vo.ArticleVO;

public interface ArticleService extends IService<Article> {

    /**
     * 分页查询文章列表（包含分类名和标签）
     */
    Page<ArticleVO> getArticlePage(Integer current, Integer size);

    /**
     * 根据 ID 获取文章详情 (用于编辑)
     */
    ArticleVO getArticleById(Long id);

    /**
     * 后台发布新文章
     */
    void saveArticle(ArticleDTO dto);

    /**
     * 后台更新文章
     */
    void updateArticle(ArticleDTO dto);

    /**
     * 后台删除文章 (级联删除标签关联)
     */
    void deleteArticle(Long id);

}
