package ru.vsu.zhertvydedlina.aiservice.file.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
//для проверки расширения
@Service
public class FileValidationService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf",
            "png",
            "jpg",
            "jpeg",
            "dks"
    ); //мб, стоило бы сделать enum

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    public void validate(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "File size exceeds 50 MB"
            );
        }

        String extension = getExtension(file.getOriginalFilename());

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Unsupported file format: " + extension
            );
        }
    }

    private String getExtension(String fileName) {

        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName
                .substring(fileName.lastIndexOf('.') + 1)
                .toLowerCase();
    }
}
