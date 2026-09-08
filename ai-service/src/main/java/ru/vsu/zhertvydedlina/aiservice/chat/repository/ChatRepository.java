package ru.vsu.zhertvydedlina.aiservice.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Chat;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByUserId(Long userId);

    boolean existsChatByIdAndUserId(Long id, Long userId);
}
