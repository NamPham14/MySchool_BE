package com.fpt.myfschool.mapper;
import com.fpt.myfschool.dto.response.ConversationDto;
import com.fpt.myfschool.dto.response.MessageDto;
import com.fpt.myfschool.entity.Conversation;
import com.fpt.myfschool.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    ConversationDto toConversationDto(Conversation conversation);

    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderName", source = "sender.fullName")
    MessageDto toMessageDto(Message message);
}