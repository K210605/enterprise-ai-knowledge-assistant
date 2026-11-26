package com.khushi.knowledgeassistant.service;

import com.google.genai.Client;
import com.google.genai.types.EmbedContentResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {

    private final Client geminiClient;

    public EmbeddingService(Client geminiClient) {
        this.geminiClient = geminiClient;
    }

    public List<Float> generateEmbedding(String text) {

        EmbedContentResponse response =
                geminiClient.models.embedContent(
                        "gemini-embedding-001",
                        text,
                        null
                );

        return response.embeddings()
                .orElseThrow(() ->
                        new RuntimeException("Embedding generation failed!"))
                .get(0)
                .values()
                .orElseThrow(() ->
                        new RuntimeException("Embedding values not found!"));
    }

    public List<List<Float>> generateEmbeddings(List<String> texts) {

        EmbedContentResponse response =
                geminiClient.models.embedContent(
                        "gemini-embedding-001",
                        texts,
                        null
                );

        return response.embeddings()
                .orElseThrow(() ->
                        new RuntimeException("Batch embedding generation failed!"))
                .stream()
                .map(embedding -> embedding.values()
                        .orElseThrow(() ->
                                new RuntimeException("Embedding values not found!")))
                .toList();
    }
}