package com.zj.managesys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@ServletComponentScan
@SpringBootApplication
public class ClubSysApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClubSysApplication.class, args);
        log.info("高校社团管理系统项目启动成功...");
    }
}
