package ru.vsu.zhertvydedlina.aiservice.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.zhertvydedlina.aiservice.common.exception.ForbiddenException;
import ru.vsu.zhertvydedlina.aiservice.file.dto.response.BlueprintFileResponseDto;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;
import ru.vsu.zhertvydedlina.aiservice.file.service.BlueprintFileService;
import ru.vsu.zhertvydedlina.aiservice.user.model.entity.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/blueprint-files")
@RequiredArgsConstructor
public class BlueprintFileController {

    private final BlueprintFileService blueprintFileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<BlueprintFileResponseDto>> uploadBlueprintFiles(
            @AuthenticationPrincipal User user,
            @RequestParam("files") List<MultipartFile> files
    ) {
        List<BlueprintFile> uploaded = blueprintFileService.uploadBlueprintFile(files, user.getId());

        return ResponseEntity.ok(uploaded.stream()
                .map(file -> BlueprintFileResponseDto.builder()
                        .id(file.getId())
                        .fileName(file.getFileName())
                        .contentType(file.getContentType())
                        .fileSize(file.getFileSize())
                        .build())
                .toList());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadBlueprintFile(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        if (!blueprintFileService.checkBlueprintFileOwner(id, user.getId())) {
            throw new ForbiddenException("Данный файл не принадлежит пользователю или не существует");
        }

        BlueprintFile file = blueprintFileService.getBlueprintFileById(id);
        InputStreamResource resource = blueprintFileService.loadBlueprintFileById(id);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(file.getFileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(file.getContentType() != null
                        ? MediaType.parseMediaType(file.getContentType())
                        : MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentLength(file.getFileSize())
                .body(resource);
    }
}
