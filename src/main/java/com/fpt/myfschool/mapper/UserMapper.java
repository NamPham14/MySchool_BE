package com.fpt.myfschool.mapper;
import com.fpt.myfschool.dto.response.UserProfileResponse;
import com.fpt.myfschool.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserProfileResponse toProfileDto(User user);
}