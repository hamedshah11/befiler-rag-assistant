package com.befiler.assistant.controller;

import com.befiler.assistant.model.ChatRequest;
import com.befiler.assistant.model.ChatResponse;
import com.befiler.assistant.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChatController {
    
    private final RagService ragService;
    
    @PostMapping("/query")
    public ResponseEntity<ChatResponse> processQuery(@RequestBody ChatRequest request) {
        try {
            String response = ragService.processQuery(request.getQuery(), request.getSection(), request.getSubsection());
            return ResponseEntity.ok(new ChatResponse(response, request.getSection(), request.getSubsection()));
        } catch (Exception e) {
            return ResponseEntity.ok(new ChatResponse(
                "I apologize, but I'm having trouble processing your request right now. Please try again later.",
                request.getSection(),
                request.getSubsection()
            ));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("RAG Assistant is running");
    }
}
