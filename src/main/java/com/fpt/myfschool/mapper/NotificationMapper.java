package com.fpt.myfschool.mapper;
import com.fpt.myfschool.dto.response.NotificationResponse;
import com.fpt.myfschool.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toDto(Notification notification);
}