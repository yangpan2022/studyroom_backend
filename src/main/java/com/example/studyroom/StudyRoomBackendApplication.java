package com.example.studyroom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 自习室管理系统 - Spring Boot 启动入口
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("com.example.studyroom.mapper") // 扫描所有 MyBatis Mapper 接口
public class StudyRoomBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyRoomBackendApplication.class, args);
    }

}
