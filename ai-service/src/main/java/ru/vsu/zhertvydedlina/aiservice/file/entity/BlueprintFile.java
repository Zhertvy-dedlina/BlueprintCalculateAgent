package ru.vsu.zhertvydedlina.aiservice.file.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "blueprint_files")
public class BlueprintFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "storage_key", nullable = false, unique = true)
    private String storageKey;

    @Column(name = "giga_chat_file_id")
    private String gigaChatFileId; //идентификаторы внешнего API обычно String, если документация API явно не гарантирует число
}
