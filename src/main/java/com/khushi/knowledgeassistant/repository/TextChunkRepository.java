package com.khushi.knowledgeassistant.repository;

import com.khushi.knowledgeassistant.entity.TextChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TextChunkRepository extends JpaRepository<TextChunk, Long> {

    List<TextChunk> findByDocumentId(Long documentId);

    @Query("SELECT c FROM TextChunk c JOIN FETCH c.document d " +
           "WHERE d.id = :documentId ORDER BY c.chunkIndex")
    List<TextChunk> findByDocumentIdWithDocument(@Param("documentId") Long documentId);

    @Query("SELECT c FROM TextChunk c JOIN FETCH c.document d " +
           "ORDER BY d.id, c.chunkIndex")
    List<TextChunk> findAllWithDocument();
}