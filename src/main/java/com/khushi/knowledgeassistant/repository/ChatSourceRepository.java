
package com.khushi.knowledgeassistant.repository;

import com.khushi.knowledgeassistant.entity.ChatSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatSourceRepository extends JpaRepository<ChatSource, Long> {

    List<ChatSource> findByChatMessageId(Long messageId);

    void deleteByChatMessageId(Long messageId);
}