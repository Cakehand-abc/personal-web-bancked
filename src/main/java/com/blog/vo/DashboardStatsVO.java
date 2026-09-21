package com.blog.vo;

import com.blog.entity.Message;
import com.blog.entity.Moment;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardStatsVO {

    private Long articleCount;

    private Long momentCount;

    private Long projectCount;

    private Long messageCount;

    private Long categoryCount;

    private Long tagCount;

    private Long totalWords;

    private Long totalLikes;

    private List<Message> recentMessages;

    private List<Moment> recentMoments;

    private List<Map<String, Object>> trendData;

    private Map<String, Object> serverInfo;

}
