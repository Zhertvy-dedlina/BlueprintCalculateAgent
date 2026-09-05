package ru.vsu.zhertvydedlina.aiservice.file.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileStorageService {

    String save(MultipartFile file, String storageKey);

    InputStream get(String storageKey);

    void delete(String storageKey);

}
