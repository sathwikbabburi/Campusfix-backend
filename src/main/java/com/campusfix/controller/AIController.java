package com.campusfix.controller;

import com.campusfix.dto.AICategorizationRequest;
import com.campusfix.dto.AICategorizationResponse;
import com.campusfix.dto.AIChatRequest;
import com.campusfix.dto.AIChatResponse;
import com.campusfix.dto.ApiResponse;
import com.campusfix.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Engine & RAG Chatbot", description = "Endpoints for smart automated categorization, priority routing, and conversational RAG assistant")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/categorize")
    @Operation(summary = "Automatically categorize a complaint title & description with priority and department routing")
    public ResponseEntity<ApiResponse<AICategorizationResponse>> categorizeComplaint(
            @Valid @RequestBody AICategorizationRequest request) {
        AICategorizationResponse response = aiService.categorizeComplaint(request.getTitle(), request.getDescription());
        return ResponseEntity.ok(ApiResponse.success("Category and priority automatically analyzed", response));
    }

    @PostMapping("/chat")
    @Operation(summary = "Interactive RAG campus assistant chat answering ticket inquiries, maintenance guidelines, and SLA rules")
    public ResponseEntity<ApiResponse<AIChatResponse>> chatWithAssistant(
            @Valid @RequestBody AIChatRequest request) {
        AIChatResponse response = aiService.processChat(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
