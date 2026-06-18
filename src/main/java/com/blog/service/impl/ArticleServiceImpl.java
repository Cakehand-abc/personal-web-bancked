package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Article;
import com.blog.entity.ArticleTag;
import com.blog.entity.Category;
import com.blog.entity.Tag;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.TagMapper;
import com.blog.service.ArticleService;
import com.blog.vo.ArticleDTO;
import com.blog.vo.ArticleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private ArticleTagMapper articleTagMapper;
    
    @Autowired
    private TagMapper tagMapper;

    @Override
    public Page<ArticleVO> getArticlePage(Integer current, Integer size) {
        // 1. 分页查询文章主表
        Page<Article> pageParam = new Page<>(current, size);
        // 按创建时间倒序排
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateTime);
        Page<Article> articlePage = this.page(pageParam, queryWrapper);

        // 2. 将 Article 转换成 ArticleVO，并补充 CategoryName 和 Tags
        List<ArticleVO> voList = new ArrayList<>();
        for (Article article : articlePage.getRecords()) {
            ArticleVO vo = new ArticleVO();
            BeanUtils.copyProperties(article, vo);

            // 查分类名
            if (article.getCategoryId() != null) {
                Category category = categoryMapper.selectById(article.getCategoryId());
                if (category != null) {
                    vo.setCategoryName(category.getName());
                }
            }

            // 查标签
            LambdaQueryWrapper<ArticleTag> atWrapper = new LambdaQueryWrapper<>();
            atWrapper.eq(ArticleTag::getArticleId, article.getId());
            List<ArticleTag> articleTags = articleTagMapper.selectList(atWrapper);
            
            if (!articleTags.isEmpty()) {
                List<Long> tagIds = articleTags.stream().map(ArticleTag::getTagId).collect(Collectors.toList());
                List<Tag> tags = tagMapper.selectBatchIds(tagIds);
                vo.setTags(tags);
            } else {
                vo.setTags(new ArrayList<>());
            }

            voList.add(vo);
        }

        // 3. 构建返回的分页对象
        Page<ArticleVO> voPage = new Page<>(current, size, articlePage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveArticle(ArticleDTO dto) {
        // 1. 保存文章主表
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        if (article.getCreateTime() == null) {
            article.setCreateTime(LocalDateTime.now());
        }
        this.save(article);

        // 2. 处理标签绑定
        handleTags(article.getId(), dto.getTags());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(ArticleDTO dto) {
        // 1. 更新文章主表
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        this.updateById(article);

        // 2. 清空旧标签关联
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, article.getId());
        articleTagMapper.delete(wrapper);

        // 3. 重新绑定新标签
        handleTags(article.getId(), dto.getTags());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id) {
        // 1. 删除文章主表
        this.removeById(id);

        // 2. 级联删除关联表中的数据
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, id);
        articleTagMapper.delete(wrapper);
    }

    /**
     * 抽取公共方法：处理标签绑定（没有的标签自动创建）
     */
    private void handleTags(Long articleId, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }
        for (String tagName : tagNames) {
            // 查询库里有没有这个标签
            LambdaQueryWrapper<Tag> tagWrapper = new LambdaQueryWrapper<>();
            tagWrapper.eq(Tag::getName, tagName);
            Tag tag = tagMapper.selectOne(tagWrapper);

            // 如果没有，就新建一个存进库里
            if (tag == null) {
                tag = new Tag();
                tag.setName(tagName);
                tagMapper.insert(tag);
            }

            // 在关联表里插入绑定关系
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(articleId);
            articleTag.setTagId(tag.getId());
            articleTagMapper.insert(articleTag);
        }
    }
}
