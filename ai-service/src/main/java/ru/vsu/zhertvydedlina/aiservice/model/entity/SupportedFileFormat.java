package ru.vsu.zhertvydedlina.aiservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "supported_file_formats")
public class SupportedFileFormat {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "format_name")
    private String formatName;
}
