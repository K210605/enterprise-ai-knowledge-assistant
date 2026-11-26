package com.khushi.knowledgeassistant.repository;

import com.khushi.knowledgeassistant.entity.TextChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextChunkRepository extends JpaRepository<TextChunk, Long> {

    List<TextChunk> findByDocumentId(Long documentId);

}