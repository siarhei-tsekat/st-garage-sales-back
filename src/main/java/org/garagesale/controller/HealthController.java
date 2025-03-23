package org.garagesale.controller;

import org.garagesale.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return new ResponseEntity<>(ApiResponse.withPayload("hello"), HttpStatus.OK);
    }
}
