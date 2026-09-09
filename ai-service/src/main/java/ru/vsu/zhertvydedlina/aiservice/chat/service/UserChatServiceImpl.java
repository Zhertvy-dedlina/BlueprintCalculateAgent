package ru.vsu.zhertvydedlina.aiservice.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.ChatMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.MessageMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.ChatWithMessagesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.MessageWithFileNamesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Chat;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Message;
import ru.vsu.zhertvydedlina.aiservice.file.dto.response.FileResponseDto;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;
import ru.vsu.zhertvydedlina.aiservice.file.service.BlueprintFileService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserChatServiceImpl implements UserChatService {

    public static final int DEFAULT_PAGE_SIZE = 20;

    private final MessageMapper messageMapper;

    private final ChatMapper chatMapper;

    private final ChatService chatService;

    private final MessageService messageService;

    private final BlueprintFileService blueprintFileService;

    @Override
    public Chat getChatById(long chatId) {
        return chatService.getChatById(chatId);
    }

    @Override
    public List<Long> getUserChats(Long userId) {
        return chatService.getChatsByUserId(userId).stream()
                .map(Chat::getId)
                .toList();
    }

    @Override
    public ChatWithMessagesResponseDto getChatMessages(Long chatId, int page) {
        return getChatMessages(chatId, page, DEFAULT_PAGE_SIZE);
    }

    @Override
    public ChatWithMessagesResponseDto getChatMessages(Long chatId, int page, int size) {
        if (chatId == null) {
            throw new IllegalArgumentException("chatId cannot be null");
        }

        Chat chat = chatService.getChatById(chatId);

        if (page <= 0 || size <= 0) {
            throw new IllegalArgumentException("page or size cannot be negative");
        }

        List<Message> chatMessages = messageService.getMessagesByChatId(
                chatId,
                PageRequest.of(page - 1, size)
        );

        List<BlueprintFile> blueprints = blueprintFileService.getBlueprintFilesByChatId(chatId);

        return chatMapper.messageToChatWithMessages(
                chat,
                chatMessages.stream()
                        .map(msg -> messageMapper.messageToMessageWithFileNames(msg, blueprints))
                        .toList()
        );
    }

    @Override
    @Transactional
    public ChatWithMessagesResponseDto createChatFromFirstMessage(MessageRequestDto message) {
        if (message.userId() == null) {
            throw new IllegalArgumentException("message userId cannot be null");
        }

        Chat chat = chatService.saveChat(new Chat(null, message.userId()));

        Message newMessage = messageService.saveMessage(
                messageMapper.updateChatIdInMessageRequestDto(
                        message,
                        chat.getId()
                )
        );

        List<BlueprintFile> fileResponseDtos = blueprintFileService.confirmBlueprintFilesById(message.filesId());

        return chatMapper.messageToChatWithMessages(
                chat,
                List.of(messageMapper.messageToMessageWithFileNames(
                                newMessage,
                                fileResponseDtos
                        )
                )
        );
    }

    @Override
    @Transactional
    public MessageWithFileNamesResponseDto addMessageToChat(Long chatId, MessageRequestDto message) {
        MessageRequestDto newMessage = messageMapper.updateChatIdInMessageRequestDto(message, chatId);

        Message savedMessage = messageService.saveMessage(newMessage);
        List<BlueprintFile> fileResponseDtos = blueprintFileService.confirmBlueprintFilesById(message.filesId());

        return messageMapper.messageToMessageWithFileNames(
                savedMessage,
                fileResponseDtos
        );
    }

    @Override
    @Transactional
    public Chat deleteChat(Long chatId) {
        if (!chatService.existsById(chatId)) {
            return null;
        }

        messageService.deleteMessage(chatId);
        blueprintFileService.deleteBlueprintFile(chatId);

        return chatService.deleteChatById(chatId);
    }

    @Override
    public boolean checkChatOwner(Long chatId, Long userId) {
        return chatService.checkChatOwner(chatId, userId);
    }
}
