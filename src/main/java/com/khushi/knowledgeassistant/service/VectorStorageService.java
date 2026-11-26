package com.khushi.knowledgeassistant.service;

import com.khushi.knowledgeassistant.entity.TextChunk;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VectorStorageService {

    private final TextChunkService textChunkService;
    private final EmbeddingService embeddingService;
    private final ChromaService chromaService;

    private static final int BATCH_SIZE = 5;
    private static final int MAX_ATTEMPTS = 3;
    private static final long BATCH_DELAY_MS = 15000;

    public VectorStorageService(
            TextChunkService textChunkService,
            EmbeddingService embeddingService,
            ChromaService chromaService) {

        this.textChunkService = textChunkService;
        this.embeddingService = embeddingService;
        this.chromaService = chromaService;
    }

    // Store or recover chunks of a specific PDF
    public Map<String, Object> storeDocumentChunks(Long documentId) {

        List<TextChunk> chunks =
                textChunkService.getChunksByDocumentId(documentId);

        if (chunks.isEmpty()) {
            throw new RuntimeException(
                    "No chunks found for document ID: " + documentId
            );
        }

        int totalStored = processChunks(chunks);

        return Map.of(
                "message", "Document embeddings processed successfully!",
                "documentId", documentId,
                "totalChunks", chunks.size(),
                "newVectorsStored", totalStored,
                "alreadyExistingOrSkipped", chunks.size() - totalStored
        );
    }

    // Store or recover all PDF chunks independently
    public Map<String, Object> storeAllChunks() {

        List<TextChunk> chunks = textChunkService.getAllChunks();

        if (chunks.isEmpty()) {
            throw new RuntimeException("No PDF chunks found!");
        }

        Map<Long, List<TextChunk>> documents = chunks.stream()
                .collect(Collectors.groupingBy(
                        chunk -> chunk.getDocument().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        int totalStored = 0;

        for (Map.Entry<Long, List<TextChunk>> entry
                : documents.entrySet()) {

            System.out.println(
                    "Processing document ID: " + entry.getKey()
            );

            totalStored += processChunks(entry.getValue());
        }

        return Map.of(
                "message", "All document embeddings processed successfully!",
                "totalChunks", chunks.size(),
                "newVectorsStored", totalStored,
                "alreadyExistingOrSkipped", chunks.size() - totalStored
        );
    }

    // Process only chunks missing from ChromaDB
    private int processChunks(List<TextChunk> chunks) {

        if (chunks == null || chunks.isEmpty()) {
            return 0;
        }

        Long documentId = chunks.get(0).getDocument().getId();

        Set<String> existingIds =
                chromaService.getExistingChunkIds(documentId);

        List<TextChunk> missingChunks = new ArrayList<>();

        for (TextChunk chunk : chunks) {

            String chunkId = "chunk_" + chunk.getId();

            if (!existingIds.contains(chunkId)) {
                missingChunks.add(chunk);
            }
        }

        System.out.println("Document ID: " + documentId);
        System.out.println("Total chunks: " + chunks.size());
        System.out.println("Already stored: " + existingIds.size());
        System.out.println("Missing chunks: " + missingChunks.size());

        if (missingChunks.isEmpty()) {
            System.out.println("All vectors already exist!");
            return 0;
        }

        int totalStored = 0;

        for (int i = 0; i < missingChunks.size(); i += BATCH_SIZE) {

            List<TextChunk> batch = missingChunks.subList(
                    i,
                    Math.min(i + BATCH_SIZE, missingChunks.size())
            );

            List<String> texts = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            List<Map<String, Object>> metadatas = new ArrayList<>();

            for (TextChunk chunk : batch) {

                texts.add(chunk.getContent());

                ids.add("chunk_" + chunk.getId());

                metadatas.add(Map.of(
                        "documentId", chunk.getDocument().getId(),
                        "chunkIndex", chunk.getChunkIndex(),
                        "fileName", chunk.getDocument().getFileName()
                ));
            }

            // Generate embeddings only for missing chunks
            List<List<Float>> embeddings =
                    generateWithRetry(texts);

            // Store vectors in ChromaDB
            chromaService.storeEmbeddings(
                    ids,
                    embeddings,
                    texts,
                    metadatas
            );

            totalStored += batch.size();

            System.out.println(
                    "New vectors stored: " + totalStored
                            + " / " + missingChunks.size()
            );

            if (i + BATCH_SIZE < missingChunks.size()) {
                waitBetweenBatches();
            }
        }

        return totalStored;
    }

    // Generate embeddings with quota-aware retry handling
    private List<List<Float>> generateWithRetry(List<String> texts) {

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            try {

                return embeddingService.generateEmbeddings(texts);

            } catch (RuntimeException exception) {

                String errorMessage =
                        String.valueOf(exception.getMessage());

                System.out.println(
                        "Embedding attempt " + attempt + " failed: "
                                + errorMessage
                );

                // Do not retry when daily quota is exhausted
                if (isQuotaExceeded(errorMessage)) {

                    throw new RuntimeException(
                            "Gemini API quota exceeded. "
                                    + "Already stored vectors are safe. "
                                    + "Retry recovery after quota resets.",
                            exception
                    );
                }

                if (attempt == MAX_ATTEMPTS) {
                    throw exception;
                }

                waitBetweenBatches();
            }
        }

        throw new RuntimeException("Embedding generation failed!");
    }

    // Identify quota-related errors
    private boolean isQuotaExceeded(String errorMessage) {

        String error = errorMessage.toLowerCase();

        return error.contains("resource_exhausted")
                || error.contains("quota exceeded")
                || error.contains("per_day")
                || error.contains("daily limit");
    }

    // Wait between embedding batches
    private void waitBetweenBatches() {

        try {

            Thread.sleep(BATCH_DELAY_MS);

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Batch processing interrupted!",
                    exception
            );
        }
    }
}