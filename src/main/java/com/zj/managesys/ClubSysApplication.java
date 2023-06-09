package com.zj.managesys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Slf4j
@ServletComponentScan
@SpringBootApplication
@EnableWebMvc
@EnableTransactionManagement//开启Spring事务
public class ClubSysApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClubSysApplication.class, args);
        log.info("高校社团管理系统项目启动成功...");
    }
}
