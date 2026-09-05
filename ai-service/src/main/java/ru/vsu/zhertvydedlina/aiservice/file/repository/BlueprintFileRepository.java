package ru.vsu.zhertvydedlina.aiservice.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vsu.zhertvydedlina.aiservice.chat.entity.Message;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;
import ru.vsu.zhertvydedlina.aiservice.user.User;

import java.util.List;
import java.util.Map;

public interface BlueprintFileRepository
        extends JpaRepository<BlueprintFile, Long> {

    List<BlueprintFile> findAllByChatId(Long chatId);
}