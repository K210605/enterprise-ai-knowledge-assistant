package com.khushi.knowledgeassistant.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_sources")
public class ChatSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chunkId;

    private String fileName;

    private String chunkIndex;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessage chatMessage;

    public ChatSource() {
    }

    public ChatSource(
            String chunkId,
            String fileName,
            String chunkIndex,
            String content,
            ChatMessage chatMessage) {

        this.chunkId = chunkId;
        this.fileName = fileName;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.chatMessage = chatMessage;
    }

    public Long getId() {
        return id;
    }

    public String getChunkId() {
        return chunkId;
    }

    public void setChunkId(String chunkId) {
        this.chunkId = chunkId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(String chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}