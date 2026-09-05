package ru.vsu.zhertvydedlina.aiservice.file.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileResponseDto {

    private Long id;

    private Long chatId;

    private String fileName;

    private String contentType;

    private Long fileSize;
}