package ru.vsu.zhertvydedlina.aiservice.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.chat.entity.Chat;
import ru.vsu.zhertvydedlina.aiservice.chat.repository.ChatRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    public final ChatRepository chatRepository;

    @Override
    public Chat getChatById(long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("chat with id: " + chatId + "doesn't exist"));
    }

    @Override
    public List<Chat> getChatsByUserId(long userId) {
        return chatRepository.findAllByUserId(userId);
    }

    @Override
    public Chat saveChat(Chat chat) {
        return chatRepository.save(chat);
    }

    @Override
    public Long deleteChatById(long chatId) {
        chatRepository.deleteById(chatId);

        return chatId;
    }

    @Override
    public boolean existsById(long chatId) {
        return chatRepository.existsById(chatId);
    }

    @Override
    public boolean checkChatOwner(long chatId, long userId) {
        return chatRepository.existsChatByIdAndUserId(chatId, userId);
    }
}
