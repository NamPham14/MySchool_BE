package com.fpt.myfschool.mapper;
import com.fpt.myfschool.dto.request.SubjectRequest;
import com.fpt.myfschool.dto.response.SubjectResponse;
import com.fpt.myfschool.entity.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    SubjectResponse toDto(Subject subject);
    
    @Mapping(target = "id", ignore = true)
    Subject toEntity(SubjectRequest request);
}