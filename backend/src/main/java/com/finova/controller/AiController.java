package com.finova.controller;

import com.finova.dto.ai.AiChatRequest;
import com.finova.dto.ai.AiChatResponse;
import com.finova.dto.ai.AiInsightsResponse;
import com.finova.security.UserPrincipal;
import com.finova.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "FINOVA AI Assistant", description = "AI-powered financial chatbot, spending pattern analysis, and budget tips")
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    @Operation(summary = "Ask FINOVA AI a financial question with authorized contextual financial data")
    public ResponseEntity<AiChatResponse> chat(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AiChatRequest request) {
        return ResponseEntity.ok(aiService.chat(userPrincipal.getId(), request));
    }

    @GetMapping("/insights")
    @Operation(summary = "Get automated AI financial insights, anomalies, and personalized recommendations")
    public ResponseEntity<AiInsightsResponse> getInsights(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(aiService.getInsights(userPrincipal.getId()));
    }

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

}
