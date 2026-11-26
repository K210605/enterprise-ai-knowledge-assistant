package com.khushi.knowledgeassistant.service;

import com.khushi.knowledgeassistant.entity.ChatMessage;
import com.khushi.knowledgeassistant.entity.ChatSource;
import com.khushi.knowledgeassistant.repository.ChatSourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatSourceService {

    private final ChatSourceRepository chatSourceRepository;

    public ChatSourceService(ChatSourceRepository chatSourceRepository) {
        this.chatSourceRepository = chatSourceRepository;
    }

    public void saveSources(
            List<Map<String, Object>> sources,
            ChatMessage chatMessage) {

        if (sources == null || sources.isEmpty()) {
            return;
        }

        for (Map<String, Object> source : sources) {

            ChatSource chatSource = new ChatSource(
                    String.valueOf(source.getOrDefault("chunkId", "Unknown")),
                    String.valueOf(source.getOrDefault("fileName", "Unknown document")),
                    String.valueOf(source.getOrDefault("chunkIndex", "Unknown")),
                    String.valueOf(source.getOrDefault("content", "")),
                    chatMessage
            );

            chatSourceRepository.save(chatSource);
        }
    }

    public List<ChatSource> getSources(Long messageId) {
        return chatSourceRepository.findByChatMessageId(messageId);
    }

    public void deleteSources(Long messageId) {
        chatSourceRepository.deleteByChatMessageId(messageId);
    }
}