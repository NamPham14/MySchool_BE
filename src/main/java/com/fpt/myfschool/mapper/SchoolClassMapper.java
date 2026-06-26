package com.fpt.myfschool.mapper;
import com.fpt.myfschool.dto.request.SchoolClassRequest;
import com.fpt.myfschool.dto.response.SchoolClassResponse;
import com.fpt.myfschool.entity.SchoolClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SchoolClassMapper {
    SchoolClassResponse toDto(SchoolClass schoolClass);

    @Mapping(target = "id", ignore = true)
    SchoolClass toEntity(SchoolClassRequest request);
}