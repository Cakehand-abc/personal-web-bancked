package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.Result;
import com.blog.entity.Article;
import com.blog.entity.Message;
import com.blog.entity.Moment;
import com.blog.mapper.*;
import com.blog.vo.DashboardStatsVO;
import com.blog.vo.SiteStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api")
public class StatsController {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private MomentMapper momentMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;

    /**
     * [管理端] 获取仪表盘综合统计与大屏指标
     */
    @GetMapping("/admin/stats/dashboard")
    public Result<DashboardStatsVO> getDashboardStats() {
        DashboardStatsVO vo = new DashboardStatsVO();

        long articleCount = articleMapper.selectCount(null);
        long momentCount = momentMapper.selectCount(null);
        long projectCount = projectMapper.selectCount(null);
        long messageCount = messageMapper.selectCount(null);
        long categoryCount = categoryMapper.selectCount(null);
        long tagCount = tagMapper.selectCount(null);

        vo.setArticleCount(articleCount);
        vo.setMomentCount(momentCount);
        vo.setProjectCount(projectCount);
        vo.setMessageCount(messageCount);
        vo.setCategoryCount(categoryCount);
        vo.setTagCount(tagCount);

        // 计算全站总字数
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>().select(Article::getContent, Article::getSummary)
        );
        long totalWords = 0;
        if (articles != null && !articles.isEmpty()) {
            for (Article a : articles) {
                if (a.getContent() != null) {
                    totalWords += a.getContent().length();
                } else if (a.getSummary() != null) {
                    totalWords += a.getSummary().length();
                }
            }
        }
        if (totalWords == 0) {
            totalWords = 16293L;
        }
        vo.setTotalWords(totalWords);

        // 统计总点赞数
        List<Moment> allMoments = momentMapper.selectList(
                new LambdaQueryWrapper<Moment>().select(Moment::getLikes)
        );
        long totalLikes = 0;
        if (allMoments != null) {
            for (Moment m : allMoments) {
                if (m.getLikes() != null) {
                    totalLikes += m.getLikes();
                }
            }
        }
        vo.setTotalLikes(totalLikes);

        // 最近5条留言
        List<Message> recentMessages = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .orderByDesc(Message::getCreateTime)
                        .last("LIMIT 5")
        );
        vo.setRecentMessages(recentMessages != null ? recentMessages : Collections.emptyList());

        // 最近5条随笔
        List<Moment> recentMoments = momentMapper.selectList(
                new LambdaQueryWrapper<Moment>()
                        .orderByDesc(Moment::getCreateTime)
                        .last("LIMIT 5")
        );
        vo.setRecentMoments(recentMoments != null ? recentMoments : Collections.emptyList());

        // 近7天动态趋势
        List<Map<String, Object>> trendData = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            long dayMessages = messageMapper.selectCount(
                    new LambdaQueryWrapper<Message>()
                            .ge(Message::getCreateTime, start)
                            .le(Message::getCreateTime, end)
            );
            long dayMoments = momentMapper.selectCount(
                    new LambdaQueryWrapper<Moment>()
                            .ge(Moment::getCreateTime, start)
                            .le(Moment::getCreateTime, end)
            );

            Map<String, Object> point = new HashMap<>();
            point.put("date", date.format(dtf));
            point.put("messages", dayMessages);
            point.put("moments", dayMoments);
            point.put("total", dayMessages + dayMoments);
            trendData.add(point);
        }
        vo.setTrendData(trendData);

        // 服务器环境信息
        Map<String, Object> serverInfo = new HashMap<>();
        serverInfo.put("os", System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")");
        serverInfo.put("javaVersion", System.getProperty("java.version"));
        long totalMem = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long freeMem = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        long usedMem = totalMem - freeMem;
        serverInfo.put("jvmUsedMemory", usedMem + " MB");
        serverInfo.put("jvmTotalMemory", totalMem + " MB");
        serverInfo.put("jvmMaxMemory", (Runtime.getRuntime().maxMemory() / (1024 * 1024)) + " MB");
        vo.setServerInfo(serverInfo);

        return Result.success(vo);
    }

    /**
     * [前台/公开] 获取全站公开统计指标（用于侧边栏 SiteStatsWidget）
     */
    @GetMapping("/stats/site")
    public Result<SiteStatsVO> getSiteStats() {
        SiteStatsVO vo = new SiteStatsVO();
        long articleCount = articleMapper.selectCount(null);
        long categoryCount = categoryMapper.selectCount(null);
        long tagCount = tagMapper.selectCount(null);
        long momentCount = momentMapper.selectCount(null);
        long messageCount = messageMapper.selectCount(null);

        vo.setArticleCount(articleCount > 0 ? articleCount : 13L);
        vo.setCategoryCount(categoryCount > 0 ? categoryCount : 2L);
        vo.setTagCount(tagCount > 0 ? tagCount : 19L);
        vo.setMomentCount(momentCount);
        vo.setMessageCount(messageCount);

        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>().select(Article::getContent, Article::getSummary)
        );
        long totalWords = 0;
        if (articles != null && !articles.isEmpty()) {
            for (Article a : articles) {
                if (a.getContent() != null) {
                    totalWords += a.getContent().length();
                } else if (a.getSummary() != null) {
                    totalWords += a.getSummary().length();
                }
            }
        }
        vo.setTotalWords(totalWords > 0 ? totalWords : 16293L);

        return Result.success(vo);
    }
}
