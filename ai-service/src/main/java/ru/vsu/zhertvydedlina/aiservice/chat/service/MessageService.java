package ru.vsu.zhertvydedlina.aiservice.chat.service;

import org.springframework.data.domain.PageRequest;
import ru.vsu.zhertvydedlina.aiservice.chat.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.entity.Message;

import java.io.InputStream;
import java.util.List;

public interface MessageService {
    Message getMessageById(long id);
    List<Message> getMessagesByChatId(Long chatId, PageRequest pageRequest);

    Message saveMessage(MessageRequestDto message);
    Message deleteMessage(Long id);
}
