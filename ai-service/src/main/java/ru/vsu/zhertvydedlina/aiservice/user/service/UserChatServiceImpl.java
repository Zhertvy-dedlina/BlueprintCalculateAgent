package ru.vsu.zhertvydedlina.aiservice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.chat.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.response.ChatWithMessagesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.response.MessageWithFileNamesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.entity.Chat;
import ru.vsu.zhertvydedlina.aiservice.chat.entity.Message;
import ru.vsu.zhertvydedlina.aiservice.chat.repository.ChatRepository;
import ru.vsu.zhertvydedlina.aiservice.chat.repository.MessageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserChatServiceImpl implements UserChatService {
    private final ChatRepository chatRepository;

    private final MessageRepository messageRepository;

    @Override
    public List<Long> getUserChats(Long userId) {
        return chatRepository.findAllByUserId(userId).stream()
                .map(Chat::getId)
                .toList();
    }

    @Override
    public List<ChatWithMessagesResponseDto> getUserChatMessages(Long chatId, int page, int size) {
        if (chatId == null) {
            throw new IllegalArgumentException("chatId cannot be null");
        }

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("chat with id " + chatId + " does not exist"));

        List<Message> chatMessages = messageRepository.findAllByChatId(
                chatId,
                PageRequest.of(page, size)
        );


        return List.of();
    }

    @Override
    public ChatWithMessagesResponseDto createChatFromFirstMessage(MessageRequestDto message) {
        return null;
    }

    @Override
    public MessageWithFileNamesResponseDto addMessageToChat(Long chatId, MessageRequestDto message) {
        return null;
    }

    @Override
    public Long deleteChat(Long chatId) {
        return 0L;
    }
}
