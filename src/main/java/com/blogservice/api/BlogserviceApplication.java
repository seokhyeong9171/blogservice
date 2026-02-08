package com.blogservice.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class BlogserviceApplication {

    public static void main(String[] args) {

        System.setProperty("spring.profiles.active", "measurement");

        SpringApplication.run(BlogserviceApplication.class, args);
    }

}
