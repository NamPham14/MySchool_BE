package com.fpt.myfschool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MyFSchoolApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyFSchoolApplication.class, args);
    }
}
