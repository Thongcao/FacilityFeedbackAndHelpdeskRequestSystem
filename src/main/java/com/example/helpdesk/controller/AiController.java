package com.example.helpdesk.controller;

import com.example.helpdesk.service.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for AI-powered features.
 * Called via AJAX from frontend pages.
 * 
 * @author Facility Helpdesk Team
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private static final Logger logger = LoggerFactory.getLogger(AiController.class);

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    /**
     * Suggest category based on ticket content.
     */
    @PostMapping("/suggest-category")
    public ResponseEntity<?> suggestCategory(@RequestBody Map<String, String> request) {
        String subject = request.getOrDefault("subject", "");
        String description = request.getOrDefault("description", "");

        if (subject.isEmpty() && description.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Subject or description is required"));
        }

        if (!aiService.isAvailable()) {
            return ResponseEntity.ok(Map.of("available", false, "message", "AI service not configured"));
        }

        String category = aiService.suggestCategory(subject, description);
        if (category != null) {
            return ResponseEntity.ok(Map.of("available", true, "category", category));
        }
        return ResponseEntity.ok(Map.of("available", true, "category", ""));
    }

    /**
     * Suggest priority based on ticket content.
     */
    @PostMapping("/suggest-priority")
    public ResponseEntity<?> suggestPriority(@RequestBody Map<String, String> request) {
        String subject = request.getOrDefault("subject", "");
        String description = request.getOrDefault("description", "");

        if (!aiService.isAvailable()) {
            return ResponseEntity.ok(Map.of("available", false));
        }

        String priority = aiService.suggestPriority(subject, description);
        if (priority != null) {
            return ResponseEntity.ok(Map.of("available", true, "priority", priority.trim().toUpperCase()));
        }
        return ResponseEntity.ok(Map.of("available", true, "priority", "MEDIUM"));
    }

    /**
     * Suggest a reply for staff responding to a ticket.
     */
    @PostMapping("/suggest-reply")
    public ResponseEntity<?> suggestReply(@RequestBody Map<String, String> request) {
        String subject = request.getOrDefault("subject", "");
        String description = request.getOrDefault("description", "");
        String status = request.getOrDefault("status", "CREATED");

        if (!aiService.isAvailable()) {
            return ResponseEntity.ok(Map.of("available", false));
        }

        String reply = aiService.suggestReply(subject, description, status);
        if (reply != null) {
            return ResponseEntity.ok(Map.of("available", true, "reply", reply));
        }
        return ResponseEntity.ok(Map.of("available", true, "reply", ""));
    }

    /**
     * Check if AI service is available.
     */
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        return ResponseEntity.ok(Map.of("available", aiService.isAvailable()));
    }
}
