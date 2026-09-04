package ru.vsu.zhertvydedlina.aiservice.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.model.dto.request.MessageDto;
import ru.vsu.zhertvydedlina.aiservice.model.dto.response.ChatWithMessagesDto;
import ru.vsu.zhertvydedlina.aiservice.model.dto.response.MessageWithFileNamesDto;
import ru.vsu.zhertvydedlina.aiservice.model.entity.Chat;
import ru.vsu.zhertvydedlina.aiservice.model.entity.Message;
import ru.vsu.zhertvydedlina.aiservice.repository.ChatRepository;
import ru.vsu.zhertvydedlina.aiservice.repository.MessageRepository;

import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class UserChatServiceImpl implements UserChatService {
    private ChatRepository chatRepository;

    private MessageRepository messageRepository;

    @Override
    public List<Long> getUserChats(Long userId) {
        return chatRepository.findAllByUserId(userId).stream()
                .map(Chat::getId)
                .toList();
    }

    @Override
    public List<ChatWithMessagesDto> getUserChatMessages(Long chatId, int page, int size) {
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
    public ChatWithMessagesDto createChatFromFirstMessage(MessageDto message) {
        return null;
    }

    @Override
    public MessageWithFileNamesDto addMessageToChat(Long chatId, MessageDto message) {
        return null;
    }

    @Override
    public Long deleteChat(Long chatId) {
        return 0L;
    }
}
