package com.fpt.myfschool.repository;
import com.fpt.myfschool.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    java.util.Optional<Conversation> findByName(String name);
}