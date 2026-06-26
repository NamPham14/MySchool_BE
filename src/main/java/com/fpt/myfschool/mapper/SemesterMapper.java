package com.fpt.myfschool.mapper;
import com.fpt.myfschool.dto.request.SemesterRequest;
import com.fpt.myfschool.dto.response.SemesterResponse;
import com.fpt.myfschool.entity.Semester;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SemesterMapper {
    SemesterResponse toDto(Semester semester);

    @Mapping(target = "id", ignore = true)
    Semester toEntity(SemesterRequest request);
}