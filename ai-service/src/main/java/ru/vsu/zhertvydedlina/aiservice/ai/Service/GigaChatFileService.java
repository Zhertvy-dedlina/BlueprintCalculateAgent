package ru.vsu.zhertvydedlina.aiservice.ai.Service;

import chat.giga.client.GigaChatClient;
import chat.giga.model.file.UploadFileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GigaChatFileService {

    private final GigaChatClient gigaChatClient;

    public String upload(MultipartFile file) {

        try {
            var uploadedFile = gigaChatClient.uploadFile(
                    UploadFileRequest.builder()
                            .file(file.getBytes())
                            .mimeType(file.getContentType())
                            .fileName(file.getOriginalFilename())
                            .purpose("general")
                            .build()
            );

            return uploadedFile.id().toString();

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to read file",
                    e
            );
        }
    }

    public String uploadFromFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = fis.readAllBytes();
            String mimeType = java.nio.file.Files.probeContentType(file.toPath());

            var uploadedFile = gigaChatClient.uploadFile(
                    UploadFileRequest.builder()
                            .file(fileBytes)
                            .mimeType(mimeType != null ? mimeType : "application/pdf")
                            .fileName(file.getName())
                            .purpose("assistants")
                            .build()
            );
            return uploadedFile.id().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read converted file: " + file.getName(), e);
        }
    }
}