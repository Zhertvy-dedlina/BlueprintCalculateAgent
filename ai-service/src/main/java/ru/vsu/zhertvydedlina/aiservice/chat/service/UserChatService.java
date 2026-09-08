package ru.vsu.zhertvydedlina.aiservice.chat.service;

import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Chat;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.ChatWithMessagesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.MessageWithFileNamesResponseDto;

import java.util.List;

@Service
public interface UserChatService {

    Chat getChatById(long chatId);

    List<Long> getUserChats(Long userId);

    ChatWithMessagesResponseDto getChatMessages(Long chatId, int page);

    ChatWithMessagesResponseDto getChatMessages(Long chatId, int page, int size);

    ChatWithMessagesResponseDto createChatFromFirstMessage(MessageRequestDto message);

    MessageWithFileNamesResponseDto addMessageToChat(Long chatId, MessageRequestDto message);

    Chat deleteChat(Long chatId);

    boolean checkChatOwner(Long chatId, Long userId);
}
