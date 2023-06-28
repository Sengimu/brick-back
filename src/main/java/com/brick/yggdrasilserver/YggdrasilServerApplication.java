package com.brick.yggdrasilserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class YggdrasilServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(YggdrasilServerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.warn("启动成功,第一次使用请打开/setup.html进行初始化");
    }
}
