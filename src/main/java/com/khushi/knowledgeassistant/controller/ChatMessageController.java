package com.khushi.knowledgeassistant.controller;

import com.khushi.knowledgeassistant.entity.ChatMessage;
import com.khushi.knowledgeassistant.entity.ChatSource;
import com.khushi.knowledgeassistant.service.ChatMessageService;
import com.khushi.knowledgeassistant.service.ChatSourceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(origins = "*")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final ChatSourceService chatSourceService;

    public ChatMessageController(
            ChatMessageService chatMessageService,
            ChatSourceService chatSourceService) {

        this.chatMessageService = chatMessageService;
        this.chatSourceService = chatSourceService;
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<Map<String, Object>>> getMessages(
            @PathVariable Long conversationId) {

        List<ChatMessage> chatMessages =
                chatMessageService.getMessages(conversationId);

        List<Map<String, Object>> messages = chatMessages.stream()
                .map(message -> {

                    Map<String, Object> messageData =
                            new LinkedHashMap<>();

                    messageData.put("id", message.getId());
                    messageData.put("content", message.getContent());
                    messageData.put("role", message.getRole());
                    messageData.put("createdAt", message.getCreatedAt());

                    // Retrieve sources associated with this message
                    if ("assistant".equals(message.getRole())) {

                        List<Map<String, Object>> sources =
                                chatSourceService.getSources(message.getId())
                                        .stream()
                                        .map(this::convertSourceToMap)
                                        .toList();

                        messageData.put("sources", sources);

                    } else {

                        messageData.put("sources", List.of());
                    }

                    return messageData;
                })
                .toList();

        return ResponseEntity.ok(messages);
    }

    private Map<String, Object> convertSourceToMap(ChatSource source) {

        Map<String, Object> sourceData = new LinkedHashMap<>();

        sourceData.put("id", source.getId());
        sourceData.put("chunkId", source.getChunkId());
        sourceData.put("fileName", source.getFileName());
        sourceData.put("chunkIndex", source.getChunkIndex());
        sourceData.put("content", source.getContent());

        return sourceData;
    }
}