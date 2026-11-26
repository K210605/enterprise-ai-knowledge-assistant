package com.khushi.knowledgeassistant.service;

import com.khushi.knowledgeassistant.entity.Document;
import com.khushi.knowledgeassistant.entity.TextChunk;
import com.khushi.knowledgeassistant.repository.TextChunkRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunkService {

    private final TextChunkRepository textChunkRepository;

    public TextChunkService(TextChunkRepository textChunkRepository) {
        this.textChunkRepository = textChunkRepository;
    }

    // Create chunks from PDF text
    @Transactional
    public List<TextChunk> createChunks(Document document) {

        String content = document.getContent();

        int chunkSize = 1000;
        int overlap = 200;

        List<TextChunk> chunks = new ArrayList<>();

        int start = 0;
        int chunkIndex = 0;

        while (start < content.length()) {

            int end = Math.min(start + chunkSize, content.length());

            String chunkContent = content.substring(start, end);

            TextChunk chunk = new TextChunk(
                    chunkContent,
                    chunkIndex,
                    document
            );

            chunks.add(chunk);

            chunkIndex++;

            start = end - overlap;

            if (end == content.length()) {
                break;
            }
        }

        return textChunkRepository.saveAll(chunks);
    }

    // Retrieve all saved chunks
    public List<TextChunk> getAllChunks() {

        return textChunkRepository.findAll();
    }

    // Retrieve chunks of a particular document
    public List<TextChunk> getChunksByDocumentId(Long documentId) {

        return textChunkRepository.findByDocumentId(documentId);
    }
}