package ru.vsu.zhertvydedlina.aiservice.file.service.storage;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class MinioFileStorageService implements FileStorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioFileStorageService(
            MinioClient minioClient,
            @Value("${minio.bucket}") String bucketName
    ) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @Override
    public String save(MultipartFile file, String storageKey) {

        try (InputStream inputStream = file.getInputStream()) {

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storageKey)
                            .stream(
                                    inputStream,
                                    file.getSize(),
                                    (long) -1
                            )
                            .contentType(file.getContentType())
                            .build()
            );

            return storageKey;

        } catch (Exception e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    @Override
    public InputStream get(String storageKey) {

        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storageKey)
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to get file", e);
        }
    }

    @Override
    public void delete(String storageKey) {

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storageKey)
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }
}