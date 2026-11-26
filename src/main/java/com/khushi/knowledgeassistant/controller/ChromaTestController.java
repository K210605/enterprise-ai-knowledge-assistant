package com.khushi.knowledgeassistant.controller;

import com.khushi.knowledgeassistant.service.ChromaService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chroma")
public class ChromaTestController {

    private final ChromaService chromaService;

    public ChromaTestController(ChromaService chromaService) {
        this.chromaService = chromaService;
    }

    @PostMapping("/create-collection")
    public Map<String, Object> createCollection() {

        Map result = chromaService.createCollection();

        return Map.of(
                "message", "ChromaDB collection created successfully!",
                "collection", result
        );
    }
}