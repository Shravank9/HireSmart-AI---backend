package com.hiresmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HireSmartApplication {
    public static void main(String[] args) {
        SpringApplication.run(HireSmartApplication.class, args);
        System.out.println("==================================================");
        System.out.println("🚀 HireSmart AI Backend Started Successfully!");
        System.out.println("🔗 API Base: http://localhost:8080/api");
        System.out.println("==================================================");
    }
}
