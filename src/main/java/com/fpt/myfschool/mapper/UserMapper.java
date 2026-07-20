package com.fpt.myfschool.mapper;
import com.fpt.myfschool.dto.response.UserProfileResponse;
import com.fpt.myfschool.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @org.mapstruct.Mapping(source = "schoolClass.name", target = "className")
    @org.mapstruct.Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserProfileResponse toProfileDto(User user);

    default java.util.List<String> mapRoles(java.util.Set<com.fpt.myfschool.entity.Role> roles) {
        if (roles == null) return java.util.Collections.emptyList();
        return roles.stream().map(com.fpt.myfschool.entity.Role::getName).collect(java.util.stream.Collectors.toList());
    }
}