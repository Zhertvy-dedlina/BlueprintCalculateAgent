package ru.vsu.zhertvydedlina.aiservice.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;

import java.util.List;

public interface BlueprintFileRepository
        extends JpaRepository<BlueprintFile, Long> {

    List<BlueprintFile> findAllByIdIn(List<Long> ids);

    List<BlueprintFile> findAllByMessageId(Long messageId);

    long countByIdInAndMessageId(List<Long> ids, Long messageId);

    boolean existsByIdAndUserId(Long id, Long userId);

    void deleteAllByMessageId(Long messageId);
}
