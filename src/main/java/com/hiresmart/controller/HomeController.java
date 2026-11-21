package com.hiresmart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<?> index() {
        return ResponseEntity.ok(Map.of(
            "name", "HireSmart AI API",
            "version", "1.0.0",
            "status", "RUNNING",
            "frontendPortal", "http://localhost:5173",
            "endpoints", Map.of(
                "auth", "/api/auth",
                "jobs", "/api/jobs",
                "h2Console", "/h2-console",
                "swagger", "/swagger-ui.html"
            )
        ));
    }
}
