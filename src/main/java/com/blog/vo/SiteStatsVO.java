package com.blog.vo;

import lombok.Data;

@Data
public class SiteStatsVO {

    private Long articleCount;

    private Long categoryCount;

    private Long tagCount;

    private Long totalWords;

    private Long momentCount;

    private Long messageCount;

}
