package com.khushi.knowledgeassistant.service;

import com.khushi.knowledgeassistant.entity.Conversation;
import com.khushi.knowledgeassistant.repository.ConversationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageService chatMessageService;

    public ConversationService(
            ConversationRepository conversationRepository,
            ChatMessageService chatMessageService) {

        this.conversationRepository = conversationRepository;
        this.chatMessageService = chatMessageService;
    }

    public Conversation createConversation(String title) {

        if (title == null || title.isBlank()) {
            title = "New Chat";
        }

        Conversation conversation = new Conversation(title);

        return conversationRepository.save(conversation);
    }

    public List<Conversation> getAllConversations() {

        return conversationRepository.findAll();
    }

    public Conversation getConversation(Long id) {

        return conversationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Conversation not found!"));
    }

    @Transactional
    public void deleteConversation(Long id) {

        Conversation conversation = getConversation(id);

        // First delete messages and their sources
        chatMessageService.deleteMessages(id);

        // Then delete the conversation
        conversationRepository.delete(conversation);
    }
}