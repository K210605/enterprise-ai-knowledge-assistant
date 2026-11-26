package com.khushi.knowledgeassistant.controller;

import com.khushi.knowledgeassistant.entity.Conversation;
import com.khushi.knowledgeassistant.service.ChatMessageService;
import com.khushi.knowledgeassistant.service.ConversationService;
import com.khushi.knowledgeassistant.service.SemanticSearchService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SemanticSearchController {

    private final SemanticSearchService semanticSearchService;
    private final ConversationService conversationService;
    private final ChatMessageService chatMessageService;

    public SemanticSearchController(
            SemanticSearchService semanticSearchService,
            ConversationService conversationService,
            ChatMessageService chatMessageService) {

        this.semanticSearchService = semanticSearchService;
        this.conversationService = conversationService;
        this.chatMessageService = chatMessageService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> search(
            @RequestBody Map<String, Object> request) {

        String question = (String) request.get("question");

        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Question cannot be empty!")
            );
        }

        // Step 1: Generate RAG response
        Map<String, Object> results =
                semanticSearchService.search(question);

        // Step 2: Get or create conversation
        Object conversationIdObject = request.get("conversationId");

        Conversation conversation;

        if (conversationIdObject == null) {

            String title = question.length() > 60
                    ? question.substring(0, 60) + "..."
                    : question;

            conversation =
                    conversationService.createConversation(title);

        } else {

            Long conversationId =
                    Long.valueOf(conversationIdObject.toString());

            conversation =
                    conversationService.getConversation(conversationId);
        }

        // Step 3: Save user's question
        chatMessageService.saveMessage(
                question,
                "user",
                conversation.getId()
        );

        // Step 4: Extract AI answer
        String answer = String.valueOf(
                results.getOrDefault("answer", "No answer generated.")
        );

        // Step 5: Extract retrieved sources
        List<Map<String, Object>> sources =
                (List<Map<String, Object>>) results.get("sources");

        // Step 6: Save assistant answer WITH sources
        var assistantMessage =
                chatMessageService.saveAssistantMessage(
                        answer,
                        conversation.getId(),
                        sources
                );

        // Step 7: Return response
        Map<String, Object> response =
                new LinkedHashMap<>(results);

        response.put("conversationId", conversation.getId());
        response.put("assistantMessageId", assistantMessage.getId());

        return ResponseEntity.ok(response);
    }
}