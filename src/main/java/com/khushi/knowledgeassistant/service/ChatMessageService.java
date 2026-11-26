package com.khushi.knowledgeassistant.service;

import com.khushi.knowledgeassistant.entity.ChatMessage;
import com.khushi.knowledgeassistant.entity.ChatSource;
import com.khushi.knowledgeassistant.entity.Conversation;
import com.khushi.knowledgeassistant.repository.ChatMessageRepository;
import com.khushi.knowledgeassistant.repository.ConversationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConversationRepository conversationRepository;
    private final ChatSourceService chatSourceService;

    public ChatMessageService(
            ChatMessageRepository chatMessageRepository,
            ConversationRepository conversationRepository,
            ChatSourceService chatSourceService) {

        this.chatMessageRepository = chatMessageRepository;
        this.conversationRepository = conversationRepository;
        this.chatSourceService = chatSourceService;
    }

    public ChatMessage saveMessage(
            String content,
            String role,
            Long conversationId) {

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found!"));

        ChatMessage message = new ChatMessage(content, role, conversation);

        return chatMessageRepository.save(message);
    }

    @Transactional
    public ChatMessage saveAssistantMessage(
            String content,
            Long conversationId,
            List<Map<String, Object>> sources) {

        ChatMessage message = saveMessage(
                content,
                "assistant",
                conversationId
        );

        chatSourceService.saveSources(sources, message);

        return message;
    }

    public List<ChatMessage> getMessages(Long conversationId) {

        if (!conversationRepository.existsById(conversationId)) {
            throw new RuntimeException("Conversation not found!");
        }

        return chatMessageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Transactional
    public void deleteMessages(Long conversationId) {

        List<ChatMessage> messages =
                chatMessageRepository
                        .findByConversationIdOrderByCreatedAtAsc(conversationId);

        for (ChatMessage message : messages) {
            chatSourceService.deleteSources(message.getId());
        }

        chatMessageRepository.deleteByConversationId(conversationId);
    }
}