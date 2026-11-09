package com.finova.service;

import com.finova.dto.ai.AiChatRequest;
import com.finova.dto.ai.AiChatResponse;
import com.finova.dto.ai.AiInsightsResponse;

public interface AiService {
    AiChatResponse chat(Long userId, AiChatRequest request);
    AiInsightsResponse getInsights(Long userId);
}
