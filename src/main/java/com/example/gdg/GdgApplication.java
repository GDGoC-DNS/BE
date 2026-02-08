package com.example.gdg;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // CreatedDate, LastModifiedDate 활성화
public class GdgApplication {

    public static void main(String[] args) {
        SpringApplication.run(GdgApplication.class, args);
    }


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}