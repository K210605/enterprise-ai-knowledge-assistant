package com.khushi.knowledgeassistant.service;

import com.khushi.knowledgeassistant.entity.Document;
import com.khushi.knowledgeassistant.repository.DocumentRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    // Save document
    public Document saveDocument(String fileName, String content) {

        Document document = new Document(fileName, content);

        return documentRepository.save(document);
    }

    // Get all documents
    public List<Document> getAllDocuments() {

        return documentRepository.findAll();
    }

    // Get document by ID
    public Document getDocumentById(Long id) {

        return documentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Document not found"));
    }

    // Delete document
    public void deleteDocument(Long id) {

        documentRepository.deleteById(id);
    }
}