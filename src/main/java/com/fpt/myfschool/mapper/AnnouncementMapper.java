package com.fpt.myfschool.mapper;

import com.fpt.myfschool.dto.response.AnnouncementResponse;
import com.fpt.myfschool.entity.Announcement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnnouncementMapper {
    @Mapping(source = "schoolClass.id", target = "classId")
    @Mapping(source = "schoolClass.name", target = "className")
    @Mapping(source = "teacher.id", target = "teacherId")
    @Mapping(source = "teacher.fullName", target = "teacherName")
    @Mapping(source = "teacher.avatarUrl", target = "teacherAvatar")
    AnnouncementResponse toResponse(Announcement announcement);
}
