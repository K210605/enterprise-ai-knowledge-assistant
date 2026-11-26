package com.khushi.knowledgeassistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ChromaService {

    private final RestClient restClient;

    private static final String COLLECTION_NAME = "knowledge_chunks";

    private static final String COLLECTION_URL =
            "/api/v2/tenants/default_tenant/databases/default_database/collections";

    public ChromaService(
            @Value("${chroma.url:http://localhost:8000}") String chromaUrl) {

        this.restClient = RestClient.create(chromaUrl);
    }

    // Create or retrieve ChromaDB collection
    public Map createCollection() {

        Map<String, Object> request = Map.of(
                "name", COLLECTION_NAME,
                "get_or_create", true
        );

        return restClient.post()
                .uri(COLLECTION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);
    }

    // Store embeddings in ChromaDB
    public Map storeEmbeddings(
            List<String> ids,
            List<List<Float>> embeddings,
            List<String> documents,
            List<Map<String, Object>> metadatas) {

        if (ids.isEmpty()
                || ids.size() != embeddings.size()
                || ids.size() != documents.size()
                || ids.size() != metadatas.size()) {

            throw new IllegalArgumentException(
                    "All input lists must have the same non-zero size!"
            );
        }

        Map collection = createCollection();

        String collectionId = (String) collection.get("id");

        Map<String, Object> request = Map.of(
                "ids", ids,
                "embeddings", embeddings,
                "documents", documents,
                "metadatas", metadatas
        );

        return restClient.post()
                .uri(COLLECTION_URL + "/" + collectionId + "/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);
    }

    // Get existing chunk IDs for a document
    public Set<String> getExistingChunkIds(Long documentId) {

        Map collection = createCollection();

        String collectionId = (String) collection.get("id");

        Map<String, Object> request = Map.of(
                "where", Map.of(
                        "documentId", documentId
                ),
                "include", List.of("metadatas")
        );

        Map response = restClient.post()
                .uri(COLLECTION_URL + "/" + collectionId + "/get")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        Set<String> existingIds = new HashSet<>();

        if (response != null && response.get("ids") instanceof List<?> ids) {

            for (Object id : ids) {
                existingIds.add(String.valueOf(id));
            }
        }

        return existingIds;
    }

    // Search similar chunks using query embedding
    public Map searchSimilarChunks(
            List<Float> queryEmbedding,
            int topK) {

        Map collection = createCollection();

        String collectionId = (String) collection.get("id");

        Map<String, Object> request = Map.of(
                "query_embeddings", List.of(queryEmbedding),
                "n_results", topK,
                "include", List.of(
                        "documents",
                        "metadatas",
                        "distances"
                )
        );

        return restClient.post()
                .uri(COLLECTION_URL + "/" + collectionId + "/query")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);
    }
}