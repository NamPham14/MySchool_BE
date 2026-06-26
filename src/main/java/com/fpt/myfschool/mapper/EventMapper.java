package com.fpt.myfschool.mapper;
import com.fpt.myfschool.dto.response.EventResDto;
import com.fpt.myfschool.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "categoryName", source = "category.name")
    EventResDto toDto(Event event);
}