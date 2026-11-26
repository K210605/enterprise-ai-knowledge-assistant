
package com.khushi.knowledgeassistant.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SemanticSearchService {

    private final EmbeddingService embeddingService;
    private final ChromaService chromaService;
    private final GeminiService geminiService;

    public SemanticSearchService(
            EmbeddingService embeddingService,
            ChromaService chromaService,
            GeminiService geminiService) {

        this.embeddingService = embeddingService;
        this.chromaService = chromaService;
        this.geminiService = geminiService;
    }

    public Map<String, Object> search(String question) {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "Search question cannot be empty!"
            );
        }

        // Step 1: Generate question embedding
        List<Float> queryEmbedding =
                embeddingService.generateEmbedding(question);

        // Step 2: Retrieve relevant chunks from ChromaDB
        Map<String, Object> results =
                chromaService.searchSimilarChunks(queryEmbedding, 5);

        List<String> contexts = new ArrayList<>();

        // Preserve retrieval order while removing duplicate sources
        Map<String, Map<String, Object>> uniqueSources =
                new LinkedHashMap<>();

        // Track already processed chunk IDs
        Set<String> processedChunkIds = new HashSet<>();

        Object documentsObject = results.get("documents");
        Object metadatasObject = results.get("metadatas");
        Object idsObject = results.get("ids");

        if (documentsObject instanceof List<?> documentGroups
                && !documentGroups.isEmpty()
                && documentGroups.get(0) instanceof List<?> documents) {

            List<?> metadataList = List.of();
            List<?> idList = List.of();

            if (metadatasObject instanceof List<?> metadataGroups
                    && !metadataGroups.isEmpty()
                    && metadataGroups.get(0) instanceof List<?> items) {

                metadataList = items;
            }

            if (idsObject instanceof List<?> idGroups
                    && !idGroups.isEmpty()
                    && idGroups.get(0) instanceof List<?> items) {

                idList = items;
            }

            // Step 3: Process and deduplicate retrieved chunks
            for (int i = 0; i < documents.size(); i++) {

                String content = String.valueOf(documents.get(i));

                Map<?, ?> metadata =
                        i < metadataList.size()
                                && metadataList.get(i) instanceof Map<?, ?> map
                                ? map
                                : Map.of();

                String fileName = metadata.containsKey("fileName")
                        ? String.valueOf(metadata.get("fileName"))
                        : "Unknown document";

                Object chunkIndex = metadata.get("chunkIndex");

                String chunkId = i < idList.size()
                        ? String.valueOf(idList.get(i))
                        : "Unknown";

                // Normalize whitespace to compare identical content
                String normalizedContent =
                        content.trim().replaceAll("\\s+", " ");

                // Create a content-based unique key
                String uniqueKey =
                        fileName + "|" + normalizedContent;

                // Skip if the chunk ID or content was already processed
                if (processedChunkIds.contains(chunkId)
                        || uniqueSources.containsKey(uniqueKey)) {

                    continue;
                }

                processedChunkIds.add(chunkId);

                Map<String, Object> source = new LinkedHashMap<>();

                source.put("chunkId", chunkId);
                source.put("fileName", fileName);
                source.put("chunkIndex", chunkIndex);
                source.put("content", content);

                uniqueSources.put(uniqueKey, source);

                contexts.add(
                        "Source: " + fileName
                                + "\nChunk Index: " + chunkIndex
                                + "\nContent: " + content
                );
            }
        }

        // Step 4: Handle empty search results
        if (contexts.isEmpty()) {
            return Map.of(
                    "question", question,
                    "answer", "No relevant information found in uploaded documents.",
                    "sources", List.of(),
                    "retrievedChunks", 0
            );
        }

        // Step 5: Prepare unique context for Gemini
        String context = String.join("\n\n", contexts);

        // Step 6: Generate RAG-based answer
        String answer =
                geminiService.generateRagResponse(question, context);

        // Step 7: Return unique sources
        List<Map<String, Object>> sources =
                new ArrayList<>(uniqueSources.values());

        return Map.of(
                "question", question,
                "answer", answer,
                "sources", sources,
                "retrievedChunks", sources.size()
        );
    }
}