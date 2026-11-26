package com.khushi.knowledgeassistant.controller;

import com.khushi.knowledgeassistant.entity.Conversation;
import com.khushi.knowledgeassistant.service.ConversationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(origins = "*")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createConversation(
            @RequestBody Map<String, String> request) {

        Conversation conversation =
                conversationService.createConversation(request.get("title"));

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "id", conversation.getId(),
                        "title", conversation.getTitle(),
                        "createdAt", conversation.getCreatedAt()
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllConversations() {

        List<Map<String, Object>> conversations =
                conversationService.getAllConversations()
                        .stream()
                        .map(conversation -> Map.<String, Object>of(
                                "id", conversation.getId(),
                                "title", conversation.getTitle(),
                                "createdAt", conversation.getCreatedAt(),
                                "updatedAt", conversation.getUpdatedAt()
                        ))
                        .toList();

        return ResponseEntity.ok(conversations);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteConversation(
            @PathVariable Long id) {

        conversationService.deleteConversation(id);

        return ResponseEntity.ok(
                Map.of("message", "Conversation deleted successfully!")
        );
    }
}