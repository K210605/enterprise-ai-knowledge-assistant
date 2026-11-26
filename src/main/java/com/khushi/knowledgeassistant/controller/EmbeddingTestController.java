package com.khushi.knowledgeassistant.controller;

import com.khushi.knowledgeassistant.service.EmbeddingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/embeddings")
public class EmbeddingTestController {

    private final EmbeddingService embeddingService;

    public EmbeddingTestController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping("/test")
    public Map<String, Object> testEmbedding(
            @RequestBody Map<String, String> request) {

        String text = request.get("text");

        List<Float> embedding =
                embeddingService.generateEmbedding(text);

        return Map.of(
                "message", "Embedding generated successfully!",
                "dimensions", embedding.size(),
                "sampleValues", embedding.subList(0, Math.min(5, embedding.size()))
        );
    }
}