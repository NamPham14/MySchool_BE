package com.fpt.myfschool.mapper;
import com.fpt.myfschool.dto.request.LeaveReqDto;
import com.fpt.myfschool.dto.response.LeaveResDto;
import com.fpt.myfschool.entity.LeaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveMapper {
    @Mapping(target = "studentName", source = "student.fullName")
    LeaveResDto toDto(LeaveRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    LeaveRequest toEntity(LeaveReqDto request);
}