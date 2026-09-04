package ru.vsu.zhertvydedlina.aiservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "chats",
        indexes = {@Index(name = "idx_chat_user_id", columnList = "user_id")}
)
public class Chat {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "user_id")
    private Long userId;
}
