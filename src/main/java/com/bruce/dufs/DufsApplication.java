package com.bruce.dufs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

import static com.bruce.dufs.FileUtils.init;

@SpringBootApplication
public class DufsApplication {

    @Value("${dufs.path}")
    private String uploadPath;

    public static void main(String[] args) {
        SpringApplication.run(DufsApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(){
        return args -> {
            init(uploadPath);

            System.out.println("dufs started");
        };
    }

}
