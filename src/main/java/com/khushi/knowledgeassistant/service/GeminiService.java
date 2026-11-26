package com.khushi.knowledgeassistant.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client geminiClient;

    public GeminiService(Client geminiClient) {
        this.geminiClient = geminiClient;
    }

    // Normal response generation
    public String generateResponse(String question) {

        GenerateContentResponse response =
                geminiClient.models.generateContent(
                        "gemini-3.5-flash-lite",
                        question,
                        null
                );

        return response.text();
    }

    // RAG-based response generation
    public String generateRagResponse(
            String question,
            String context) {

        String prompt = """
                You are an AI Knowledge Assistant.

                Answer the user's question using ONLY the
                information provided in the context below.

                If the answer is not available in the context,
                clearly say that the information was not found
                in the uploaded documents.

                Do not invent information.

                CONTEXT:
                %s

                USER QUESTION:
                %s

                Provide a clear and simple answer.
                """.formatted(context, question);

        GenerateContentResponse response =
                geminiClient.models.generateContent(
                        "gemini-3.5-flash-lite",
                        prompt,
                        null
                );

        return response.text();
    }
}