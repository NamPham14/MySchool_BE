package com.fpt.myfschool.mapper;
import com.fpt.myfschool.dto.response.AssignmentDto;
import com.fpt.myfschool.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {
    @Mapping(target = "className", source = "schoolClass.name")
    @Mapping(target = "subjectName", source = "subject.name")
    @Mapping(target = "teacherName", source = "teacher.fullName")
    AssignmentDto toDto(Assignment assignment);

    @Mapping(target = "schoolClass", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Assignment toEntity(com.fpt.myfschool.dto.request.AssignmentRequest request);
}