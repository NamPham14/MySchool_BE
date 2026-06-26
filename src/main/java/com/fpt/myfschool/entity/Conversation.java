package com.fpt.myfschool.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "conversations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Conversation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ConvType type;

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public enum ConvType { ONE_TO_ONE, GROUP }
}