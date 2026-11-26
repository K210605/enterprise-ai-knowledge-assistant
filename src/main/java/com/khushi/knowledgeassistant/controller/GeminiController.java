package com.khushi.knowledgeassistant.controller;

import com.khushi.knowledgeassistant.service.GeminiService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping
    public Map<String, String> chat(
            @RequestBody Map<String, String> request) {

        String question = request.get("question");

        String answer = geminiService.generateResponse(question);

        return Map.of(
                "question", question,
                "answer", answer
        );
    }
}