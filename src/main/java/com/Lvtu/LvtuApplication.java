package com.Lvtu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

@MapperScan("com.Lvtu.mapper")
@SpringBootApplication
public class LvtuApplication {

    public static void main(String[] args) {
        SpringApplication.run(LvtuApplication.class, args);
    }

}
