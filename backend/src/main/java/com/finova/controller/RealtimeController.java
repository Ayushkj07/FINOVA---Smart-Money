package com.finova.controller;

import com.finova.security.UserPrincipal;
import com.finova.service.RealtimeSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/api/realtime")
@Tag(name = "Real-Time Synchronization", description = "Server-Sent Events (SSE) stream for live financial updates across devices")
public class RealtimeController {

    private final RealtimeSyncService realtimeSyncService;

    public RealtimeController(RealtimeSyncService realtimeSyncService) {
        this.realtimeSyncService = realtimeSyncService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to live Server-Sent Events stream for real-time financial updates")
    public SseEmitter streamFinancialEvents(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return realtimeSyncService.subscribe(userPrincipal.getId());
    }

    @GetMapping("/status")
    @Operation(summary = "Check status of real-time sync hub and active connections")
    public ResponseEntity<Map<String, Object>> getSyncStatus(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        int activeConnections = realtimeSyncService.getActiveConnectionCount(userPrincipal.getId());
        return ResponseEntity.ok(Map.of(
                "status", "ACTIVE",
                "userId", userPrincipal.getId(),
                "activeStreams", activeConnections,
                "protocol", "Server-Sent Events (SSE)",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
