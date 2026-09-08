package ru.vsu.zhertvydedlina.aiservice.chat.service;

import org.springframework.data.domain.PageRequest;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Message;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageUpdateRequestDto;

import java.util.List;

public interface MessageService {
    Message getMessageById(long id);

    List<Message> getMessagesByChatId(Long chatId, PageRequest pageRequest);

    Message saveMessage(MessageRequestDto message);

    Message updateMessage(MessageUpdateRequestDto message);

    Message deleteMessage(Long id);

    boolean checkMessageOwner(Long messageId, Long userId);
}
