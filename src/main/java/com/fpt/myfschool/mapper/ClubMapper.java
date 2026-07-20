package com.fpt.myfschool.mapper;

import com.fpt.myfschool.dto.request.ClubRequest;
import com.fpt.myfschool.dto.response.ClubMemberResponse;
import com.fpt.myfschool.dto.response.ClubResponse;
import com.fpt.myfschool.entity.Club;
import com.fpt.myfschool.entity.ClubMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClubMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "leader", ignore = true)
    Club toEntity(ClubRequest request);

    @Mapping(source = "leader.id", target = "leaderId")
    @Mapping(source = "leader.fullName", target = "leaderName")
    @Mapping(target = "currentMembers", ignore = true) // Will be set manually in service
    ClubResponse toResponse(Club club);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "leader", ignore = true)
    void updateEntity(@MappingTarget Club club, ClubRequest request);

    @Mapping(source = "club.id", target = "clubId")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.fullName", target = "studentName")
    @Mapping(source = "student.email", target = "studentEmail")
    ClubMemberResponse toMemberResponse(ClubMember clubMember);
}
