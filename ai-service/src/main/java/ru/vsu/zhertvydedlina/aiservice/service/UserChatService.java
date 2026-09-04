package ru.vsu.zhertvydedlina.aiservice.service;

import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.model.dto.request.MessageDto;
import ru.vsu.zhertvydedlina.aiservice.model.dto.response.ChatWithMessagesDto;
import ru.vsu.zhertvydedlina.aiservice.model.dto.response.MessageWithFileNamesDto;

import java.util.List;

@Service
public interface UserChatService {
    List<Long> getUserChats(Long userId);

    List<ChatWithMessagesDto> getUserChatMessages(Long chatId, int page, int size);

    ChatWithMessagesDto createChatFromFirstMessage(MessageDto message);

    MessageWithFileNamesDto addMessageToChat(Long chatId, MessageDto message);

    Long deleteChat(Long chatId);
}
