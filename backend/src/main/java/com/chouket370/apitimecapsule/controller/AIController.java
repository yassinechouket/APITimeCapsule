package com.chouket370.apitimecapsule.controller;

import com.chouket370.apitimecapsule.service.AiService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@AllArgsConstructor
public class AIController {

    private final AiService aiService;



    @PostMapping("/email")
    public ResponseEntity<Map<String, String>> handleAIEmail(@RequestBody Map<String, String> req) {
        String input = req.get("input");
        if (input == null || input.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Input is required"));
        }
        try {
            String result = aiService.processEmailRequest(input);
            return ResponseEntity.ok(Map.of("email", result));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}

