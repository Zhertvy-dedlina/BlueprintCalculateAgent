package ru.vsu.zhertvydedlina.aiservice.model.entity;

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
    @GeneratedValue
    private Long id;
    @Column(name = "file_format_id")
    private Long fileFormatId;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_content")
    private byte[] fileContent;
}
