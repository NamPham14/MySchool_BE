package com.fpt.myfschool.mapper;
import com.fpt.myfschool.dto.request.TimetableRequest;
import com.fpt.myfschool.dto.response.TimetableResponse;
import com.fpt.myfschool.entity.Timetable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TimetableMapper {
    @Mapping(target = "className", source = "schoolClass.name")
    @Mapping(target = "subjectName", source = "subject.name")
    @Mapping(target = "teacherName", source = "teacher.fullName")
    TimetableResponse toDto(Timetable timetable);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolClass", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    Timetable toEntity(TimetableRequest request);
}