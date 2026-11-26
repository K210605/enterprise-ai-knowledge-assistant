package com.khushi.knowledgeassistant.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;

@Service
public class PdfService {

    // Extract text from an uploaded PDF file
    public String extractText(MultipartFile file) throws IOException {

        // Check whether the uploaded file is empty
        if (file == null || file.isEmpty()) {
            throw new IOException("Uploaded PDF file is empty!");
        }

        // Load the PDF document
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {

            // Create PDF text extractor
            PDFTextStripper pdfTextStripper = new PDFTextStripper();

            // Extract text from the PDF
            String extractedText = pdfTextStripper.getText(document);

            // Return extracted text
            return extractedText;
        }
    }
}