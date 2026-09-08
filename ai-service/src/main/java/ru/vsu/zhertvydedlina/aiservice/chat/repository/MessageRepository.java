package ru.vsu.zhertvydedlina.aiservice.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByChatId(Long chatId, Pageable pageable);

    boolean existsByIdAndUserId(Long id, Long userId);
}
