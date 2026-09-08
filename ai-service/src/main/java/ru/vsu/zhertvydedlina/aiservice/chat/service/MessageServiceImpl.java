package ru.vsu.zhertvydedlina.aiservice.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.MessageMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Message;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageUpdateRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.repository.MessageRepository;

import java.util.List;

@Primary
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    public final MessageRepository messageRepository;
    public final MessageMapper messageMapper;

    @Override
    public Message getMessageById(long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message with id " + id + " not found"));
    }

    @Override
    public List<Message> getMessagesByChatId(Long chatId, PageRequest pageRequest) {
        return messageRepository.findAllByChatId(chatId, pageRequest);
    }

    @Override
    public Message saveMessage(MessageRequestDto message) {
        return messageRepository.save(messageMapper.messageRequestDtoToMessage(message));
    }

    @Override
    public Message updateMessage(MessageUpdateRequestDto message) {
        Message oldMessage = messageRepository.findById(message.id()).orElseThrow(
                () -> new IllegalArgumentException("Message with id " + message.id() + " not found")
        );

        return messageRepository.save(messageMapper.updateMessage(oldMessage, message.message(), message.filesId()));
    }

    @Override
    public Message deleteMessage(Long id) {
        Message message = getMessageById(id);
        messageRepository.delete(message);

        return message;
    }

    @Override
    public boolean checkMessageOwner(Long messageId, Long userId) {
        return messageRepository.existsByIdAndUserId(messageId, userId);
    }
}
