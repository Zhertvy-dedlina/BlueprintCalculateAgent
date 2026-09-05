package ru.vsu.zhertvydedlina.aiservice.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.chat.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.MessageMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.entity.Message;
import ru.vsu.zhertvydedlina.aiservice.chat.repository.MessageRepository;

import java.util.List;

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
    public Message deleteMessage(Long id) {
        Message message = getMessageById(id);
        messageRepository.delete(message);

        return message;
    }
}
