package com.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        String createMessageTable = "CREATE TABLE IF NOT EXISTS `message` (" +
                "`id` bigint NOT NULL AUTO_INCREMENT," +
                "`nickname` varchar(50) DEFAULT NULL," +
                "`content` varchar(255) DEFAULT NULL," +
                "`color` varchar(20) DEFAULT NULL," +
                "`rotation` int DEFAULT '0'," +
                "`create_time` datetime DEFAULT NULL," +
                "PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        
        jdbcTemplate.execute(createMessageTable);
        System.out.println("✅ Message table initialized.");
    }
}
