package com.fpt.myfschool.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "conversation_participants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConversationParticipant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
