
package com.khushi.knowledgeassistant.controller;

import com.khushi.knowledgeassistant.entity.Document;
import com.khushi.knowledgeassistant.service.DocumentService;
import com.khushi.knowledgeassistant.service.PdfService;
import com.khushi.knowledgeassistant.service.TextChunkService;
import com.khushi.knowledgeassistant.service.VectorStorageService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "*")
public class PdfController {

    private static final long MAX_FILE_SIZE =
            20L * 1024 * 1024;

    private final PdfService pdfService;
    private final DocumentService documentService;
    private final TextChunkService textChunkService;
    private final VectorStorageService vectorStorageService;

    public PdfController(
            PdfService pdfService,
            DocumentService documentService,
            TextChunkService textChunkService,
            VectorStorageService vectorStorageService) {

        this.pdfService = pdfService;
        this.documentService = documentService;
        this.textChunkService = textChunkService;
        this.vectorStorageService = vectorStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPdf(
            @RequestParam("file") MultipartFile file) {

        try {

            // Step 1: Check empty file
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Please select a PDF file.");
            }

            // Step 2: Check file size
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.status(
                        HttpStatus.PAYLOAD_TOO_LARGE
                ).body("File size must not exceed 20 MB.");
            }

            // Step 3: Validate filename
            String originalFileName = file.getOriginalFilename();

            if (originalFileName == null
                    || originalFileName.isBlank()) {

                return ResponseEntity.badRequest()
                        .body("Invalid file name.");
            }

            // Remove any directory path from filename
            String fileName = originalFileName
                    .replace("\\", "/");

            fileName = fileName.substring(
                    fileName.lastIndexOf("/") + 1
            ).trim();

            if (!fileName.toLowerCase().endsWith(".pdf")) {
                return ResponseEntity.badRequest()
                        .body("Only PDF files are allowed.");
            }

            // Step 4: Check actual PDF file signature
            byte[] fileBytes = file.getBytes();

            if (fileBytes.length < 5
                    || fileBytes[0] != '%'
                    || fileBytes[1] != 'P'
                    || fileBytes[2] != 'D'
                    || fileBytes[3] != 'F'
                    || fileBytes[4] != '-') {

                return ResponseEntity.badRequest()
                        .body("Invalid PDF file. Please upload a genuine PDF.");
            }

            // Step 5: Extract text and validate PDF structure
            String extractedText =
                    pdfService.extractText(file);

            if (extractedText == null
                    || extractedText.isBlank()) {

                return ResponseEntity.badRequest()
                        .body("No readable text found in PDF.");
            }

            // Step 6: Save document in PostgreSQL
            Document savedDocument =
                    documentService.saveDocument(
                            fileName,
                            extractedText
                    );

            // Step 7: Create text chunks
            var createdChunks =
                    textChunkService.createChunks(savedDocument);

            System.out.println("PDF Name: " + fileName);

            System.out.println(
                    "Total Chunks Created: "
                            + createdChunks.size()
            );

            // Step 8: Generate embeddings and store in ChromaDB
            Map<String, Object> vectorResult =
                    vectorStorageService.storeDocumentChunks(
                            savedDocument.getId()
                    );

            System.out.println(
                    "Vector Storage Completed: " + vectorResult
            );

            // Step 9: Return success response
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message",
                            "PDF uploaded and indexed successfully!",

                            "documentId",
                            savedDocument.getId(),

                            "fileName",
                            savedDocument.getFileName(),

                            "totalChunks",
                            createdChunks.size(),

                            "newVectorsStored",
                            vectorResult.get("newVectorsStored"),

                            "alreadyExistingOrSkipped",
                            vectorResult.get("alreadyExistingOrSkipped")
                    ));

        } catch (IOException exception) {

            return ResponseEntity.badRequest()
                    .body("Invalid or corrupted PDF file.");

        } catch (Exception exception) {

            exception.printStackTrace();

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body("PDF processing failed. Please try again.");
        }
    }
        // Index chunks of an already uploaded document
    @PostMapping("/{documentId}/index")
    public ResponseEntity<?> indexDocument(
            @PathVariable("documentId") Long documentId) {

        try {

            return ResponseEntity.ok(
                    vectorStorageService.storeDocumentChunks(documentId)
            );

        } catch (Exception exception) {

            exception.printStackTrace();

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body("Indexing failed. Check server logs.");
        }
    }
}