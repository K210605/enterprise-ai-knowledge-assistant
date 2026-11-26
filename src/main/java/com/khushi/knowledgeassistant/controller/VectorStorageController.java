package com.khushi.knowledgeassistant.controller;

import com.khushi.knowledgeassistant.service.VectorStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vectors")
public class VectorStorageController {

    private final VectorStorageService vectorStorageService;

    public VectorStorageController(
            VectorStorageService vectorStorageService) {

        this.vectorStorageService = vectorStorageService;
    }

    // Store or recover vectors for all chunks
    @PostMapping("/store")
    public ResponseEntity<Map<String, Object>> storeVectors() {

        Map<String, Object> result =
                vectorStorageService.storeAllChunks();

        return ResponseEntity.ok(result);
    }

    // Recover missing vectors for a specific document
    @PostMapping("/recover/{documentId}")
    public ResponseEntity<Map<String, Object>> recoverVectors(
            @PathVariable Long documentId) {

        Map<String, Object> result =
                vectorStorageService.storeDocumentChunks(documentId);

        return ResponseEntity.ok(result);
    }
}