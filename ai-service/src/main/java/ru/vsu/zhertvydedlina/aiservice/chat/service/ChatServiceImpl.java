package ru.vsu.zhertvydedlina.aiservice.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.ChatMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.MessageMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.response.ChatWithMessagesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.response.MessageWithFileNamesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.entity.Chat;
import ru.vsu.zhertvydedlina.aiservice.chat.entity.Message;
import ru.vsu.zhertvydedlina.aiservice.chat.repository.ChatRepository;
import ru.vsu.zhertvydedlina.aiservice.file.dto.response.FileResponseDto;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;
import ru.vsu.zhertvydedlina.aiservice.file.service.BlueprintFileService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final MessageMapper messageMapper;

    private final ChatMapper chatMapper;

    private final ChatRepository chatRepository;

    private final MessageService messageService;

    private final BlueprintFileService blueprintFileService;

    @Override
    public List<Long> getUserChats(Long userId) {
        return chatRepository.findAllByUserId(userId).stream()
                .map(Chat::getId)
                .toList();
    }

    @Override
    public ChatWithMessagesResponseDto getChatMessages(Long chatId, int page, int size) {
        if (chatId == null) {
            throw new IllegalArgumentException("chatId cannot be null");
        }

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("chat with id " + chatId + " does not exist"));

        List<Message> chatMessages = messageService.getMessagesByChatId(
                chatId,
                PageRequest.of(page, size)
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
    public ChatWithMessagesResponseDto createChatFromFirstMessage(MessageRequestDto message) {
        if (message.userId() == null) {
            throw new IllegalArgumentException("message userId cannot be null");
        }

        Chat chat = chatRepository.save(new Chat(null, message.userId()));

        Message newMessage = messageService.saveMessage(
                messageMapper.updateChatIdInMessageRequestDto(
                        message,
                        chat.getId()
                )
        );

        List<FileResponseDto> fileResponseDtos = blueprintFileService.uploadBlueprintFile(message);

        return chatMapper.messageToChatWithMessages(
                chat,
                List.of(messageMapper.messageAndFileDtoToMessageWithFileNames(
                        newMessage,
                        fileResponseDtos)
                )
        );
    }

    @Override
    public MessageWithFileNamesResponseDto addMessageToChat(Long chatId, MessageRequestDto message) {
        MessageRequestDto newMessage = messageMapper.updateChatIdInMessageRequestDto(message, chatId);

        Message savedMessage = messageService.saveMessage(newMessage);
        List<FileResponseDto> fileResponseDtos = blueprintFileService.uploadBlueprintFile(message);

        return messageMapper.messageAndFileDtoToMessageWithFileNames(
                savedMessage,
                fileResponseDtos
        );
    }

    @Override
    public Long deleteChat(Long chatId) {
        if (!chatRepository.existsById(chatId)) {
            return null;
        }

        chatRepository.deleteById(chatId);

        return chatId;
    }
}
