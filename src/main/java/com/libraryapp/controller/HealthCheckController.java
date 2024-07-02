package com.libraryapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health Check",
        description = "Endpoint to check if the application is running.")
@RestController
@RequestMapping(value = "/health")
public class HealthCheckController {

    @Operation(summary = "Health check",
            description = "Check the health status of the application.")
    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("The application is running.");
    }
}
