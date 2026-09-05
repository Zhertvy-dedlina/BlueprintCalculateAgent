package ru.vsu.zhertvydedlina.aiservice.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name="messages",
        indexes = {@Index(name = "idx_message_id_order", columnList="(chat_id, id)")}
)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "file_id")
    private Long fileId;
    private String message;
    private Instant timestamp;
}