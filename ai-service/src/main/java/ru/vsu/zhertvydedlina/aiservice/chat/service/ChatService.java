package ru.vsu.zhertvydedlina.aiservice.chat.service;

import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Chat;

import java.util.List;

@Service
public interface ChatService {

    Chat getChatById(long chatId);

    List<Chat> getChatsByUserId(long userId);

    Chat saveChat(Chat chat);

    Chat deleteChatById(long chatId);

    boolean existsById(long chatId);

    boolean checkChatOwner(long chatId, long userId);
}
