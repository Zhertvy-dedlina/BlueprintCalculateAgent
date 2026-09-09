package ru.vsu.zhertvydedlina.aiservice.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.MessageMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Message;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageUpdateRequestDto;
import ru.vsu.zhertvydedlina.aiservice.common.exception.ForbiddenException;
import ru.vsu.zhertvydedlina.aiservice.file.service.BlueprintFileService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceWithFileProcessing implements MessageService {

    private final MessageMapper messageMapper;

    private final MessageService messageService;

    private final BlueprintFileService blueprintFileService;

    @Override
    public Message getMessageById(long id) {
        return messageService.getMessageById(id);
    }

    @Override
    public List<Message> getMessagesByChatId(Long chatId, PageRequest pageRequest) {
        return messageService.getMessagesByChatId(chatId, pageRequest);
    }

    @Override
    @Transactional
    public Message saveMessage(MessageRequestDto message) {
        Message newMessage = messageService.saveMessage(message);

        blueprintFileService.confirmBlueprintFilesById(message.filesId());

        return newMessage;
    }

    @Override
    @Transactional
    public Message updateMessage(MessageUpdateRequestDto message) {
        Message oldMessage = messageService.getMessageById(message.id());

        List<Long> newFilesId = message.filesId();
        List<Long> oldFilesId = oldMessage.getFilesId();

        // Проверка, что файлы принадлежат данному сообщению
        if (!blueprintFileService.checkBlueprintFilesOwner(message.id(), newFilesId)) {
            throw new ForbiddenException("Files are not owned by this message");
        }

        // Берем все файлы, которые были удалены при изменении сообщения
        List<Long> deletedFilesId = oldFilesId.stream()
                .filter(oldFiles -> !newFilesId.contains(oldFiles))
                .toList();
        // Берем все файлы, которых до этого не было в сообщении
        List<Long> addedFilesId = newFilesId.stream()
                .filter(newFiles -> !oldFilesId.contains(newFiles))
                .toList();

        blueprintFileService.deleteBlueprintFilesById(deletedFilesId);
        blueprintFileService.confirmBlueprintFilesById(addedFilesId);

        return messageService.updateMessage(message);
    }

    @Override
    @Transactional
    public Message deleteMessage(Long id) {
        blueprintFileService.deleteBlueprintFileByMessageId(id);

        return messageService.deleteMessage(id);
    }

    @Override
    public boolean checkMessageOwner(Long messageId, Long userId) {
        return messageService.checkMessageOwner(messageId, userId);
    }
}
