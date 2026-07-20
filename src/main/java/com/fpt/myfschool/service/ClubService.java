package com.fpt.myfschool.service;

import com.fpt.myfschool.dto.request.ClubRequest;
import com.fpt.myfschool.dto.response.ClubMemberResponse;
import com.fpt.myfschool.dto.response.ClubResponse;
import java.util.List;

public interface ClubService {
    List<ClubResponse> getAllClubs(Long currentUserId);
    List<ClubResponse> getActiveClubs(Long currentUserId);
    ClubResponse getClubById(Long id, Long currentUserId);
    ClubResponse createClub(ClubRequest request);
    ClubResponse updateClub(Long id, ClubRequest request);
    void deleteClub(Long id);

    // Membership
    List<ClubMemberResponse> getClubMembers(Long clubId);
    ClubMemberResponse joinClub(Long clubId, Long studentId);
    void approveMember(Long memberId);
    void rejectMember(Long memberId);
    void leaveClub(Long clubId, Long studentId);
}
