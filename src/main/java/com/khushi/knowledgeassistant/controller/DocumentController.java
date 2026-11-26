package com.khushi.knowledgeassistant.controller;

import com.khushi.knowledgeassistant.entity.Document;
import com.khushi.knowledgeassistant.service.DocumentService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // Save document
    @PostMapping
    public Document saveDocument(@RequestBody Document document) {

        return documentService.saveDocument(
                document.getFileName(),
                document.getContent()
        );
    }

    // Get all documents
    @GetMapping
    public List<Document> getAllDocuments() {

        return documentService.getAllDocuments();
    }

    // Get document by ID
    @GetMapping("/{id}")
    public Document getDocumentById(@PathVariable Long id) {

        return documentService.getDocumentById(id);
    }

    // Delete document
    @DeleteMapping("/{id}")
    public String deleteDocument(@PathVariable Long id) {

        documentService.deleteDocument(id);

        return "Document deleted successfully!";
    }
}