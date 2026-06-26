package com.fpt.myfschool.mapper;
import com.fpt.myfschool.dto.response.AcademicSummaryResponse;
import com.fpt.myfschool.entity.AcademicSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AcademicSummaryMapper {
    @Mapping(target = "semesterName", source = "semester.name")
    AcademicSummaryResponse toResponse(AcademicSummary entity);
}