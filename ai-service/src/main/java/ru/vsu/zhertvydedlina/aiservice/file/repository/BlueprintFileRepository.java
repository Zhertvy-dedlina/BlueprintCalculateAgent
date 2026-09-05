package ru.vsu.zhertvydedlina.aiservice.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;

import java.util.List;

public interface BlueprintFileRepository
        extends JpaRepository<BlueprintFile, Long> {

    List<BlueprintFile> findAllByChatId(Long chatId);
}