package com.architecture.solution.controller;

import com.architecture.solution.dto.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/hello")
@Tag(name = "User Controller")
public class HelloController {
    @Operation(summary = "Hello from HelloController", description = "Hello from HelloController")
    @GetMapping("")
    public ResponseEntity<ApiResponse<String>> hello() {
        log.info("Hello from HelloController");
        return ResponseEntity.ok(ApiResponse.success("Hello from HelloController"));
    }
}
