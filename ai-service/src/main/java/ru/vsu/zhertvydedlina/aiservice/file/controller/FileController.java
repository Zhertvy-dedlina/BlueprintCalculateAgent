package ru.vsu.zhertvydedlina.aiservice.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.zhertvydedlina.aiservice.file.dto.response.FileResponseDto;
import ru.vsu.zhertvydedlina.aiservice.file.service.FileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats/{chatId}/files")
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileResponseDto uploadFile(
            @PathVariable Long chatId,
            @RequestParam("file") MultipartFile file
    ) {
        return fileService.upload(chatId, file);
    }
}