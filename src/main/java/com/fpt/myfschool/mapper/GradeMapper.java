package com.fpt.myfschool.mapper;
import com.fpt.myfschool.dto.response.GradeResponse;
import com.fpt.myfschool.entity.Grade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GradeMapper {
    @Mapping(target = "subjectName", source = "subject.name")
    @Mapping(target = "subjectCode", source = "subject.code")
    GradeResponse toResponse(Grade entity);
}