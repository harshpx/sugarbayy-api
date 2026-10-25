package com.store.sugarbayy.controllers;

import com.store.sugarbayy.dtos.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public ResponseEntity<SuccessResponse<String>> home() {
        return ResponseEntity.ok(SuccessResponse.of("Sugarbayy API is running!"));
    }
}
