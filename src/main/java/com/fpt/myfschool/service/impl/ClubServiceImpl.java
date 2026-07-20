package com.fpt.myfschool.service.impl;

import com.fpt.myfschool.dto.request.ClubRequest;
import com.fpt.myfschool.dto.response.ClubMemberResponse;
import com.fpt.myfschool.dto.response.ClubResponse;
import com.fpt.myfschool.entity.Club;
import com.fpt.myfschool.entity.ClubMember;
import com.fpt.myfschool.entity.User;
import com.fpt.myfschool.exception.AppException;
import com.fpt.myfschool.exception.ErrorCode;
import com.fpt.myfschool.mapper.ClubMapper;
import com.fpt.myfschool.repository.ClubMemberRepository;
import com.fpt.myfschool.repository.ClubRepository;
import com.fpt.myfschool.repository.UserRepository;
import com.fpt.myfschool.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final UserRepository userRepository;
    private final ClubMapper clubMapper;

    @Override
    public List<ClubResponse> getAllClubs(Long currentUserId) {
        return clubRepository.findAll().stream()
                .map(club -> enrichClubResponse(club, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClubResponse> getActiveClubs(Long currentUserId) {
        return clubRepository.findByStatus("ACTIVE").stream()
                .map(club -> enrichClubResponse(club, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    public ClubResponse getClubById(Long id, Long currentUserId) {
        Club club = clubRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        return enrichClubResponse(club, currentUserId);
    }

    private ClubResponse enrichClubResponse(Club club, Long currentUserId) {
        ClubResponse response = clubMapper.toResponse(club);
        long currentMembers = clubMemberRepository.findByClubIdAndStatus(club.getId(), "APPROVED").size();
        response.setCurrentMembers((int) currentMembers);
        
        if (currentUserId != null) {
            clubMemberRepository.findByClubIdAndStudentId(club.getId(), currentUserId)
                .ifPresent(member -> response.setMembershipStatus(member.getStatus()));
        }
        
        return response;
    }

    @Override
    public ClubResponse createClub(ClubRequest request) {
        Club club = clubMapper.toEntity(request);
        if (request.getLeaderId() != null) {
            User leader = userRepository.findById(request.getLeaderId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
            club.setLeader(leader);
        }
        return enrichClubResponse(clubRepository.save(club), null);
    }

    @Override
    public ClubResponse updateClub(Long id, ClubRequest request) {
        Club club = clubRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        clubMapper.updateEntity(club, request);
        
        if (request.getLeaderId() != null) {
            User leader = userRepository.findById(request.getLeaderId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
            club.setLeader(leader);
        }
        return enrichClubResponse(clubRepository.save(club), null);
    }

    @Override
    public void deleteClub(Long id) {
        clubRepository.deleteById(id);
    }

    @Override
    public List<ClubMemberResponse> getClubMembers(Long clubId) {
        return clubMemberRepository.findByClubId(clubId).stream()
                .map(clubMapper::toMemberResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClubMemberResponse joinClub(Long clubId, Long studentId) {
        Club club = clubRepository.findById(clubId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        User student = userRepository.findById(studentId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        
        // Check if already requested
        if (clubMemberRepository.findByClubIdAndStudentId(clubId, studentId).isPresent()) {
            throw new RuntimeException("Bạn đã gửi yêu cầu tham gia câu lạc bộ này rồi.");
        }

        ClubMember member = ClubMember.builder()
                .club(club)
                .student(student)
                .status("PENDING")
                .joinDate(LocalDate.now())
                .build();
                
        return clubMapper.toMemberResponse(clubMemberRepository.save(member));
    }

    @Override
    public void approveMember(Long memberId) {
        ClubMember member = clubMemberRepository.findById(memberId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        
        // Check max members limit
        long currentMembers = clubMemberRepository.findByClubIdAndStatus(member.getClub().getId(), "APPROVED").size();
        if (member.getClub().getMaxMembers() != null && currentMembers >= member.getClub().getMaxMembers()) {
            throw new RuntimeException("Câu lạc bộ đã đạt số lượng thành viên tối đa.");
        }
        
        member.setStatus("APPROVED");
        clubMemberRepository.save(member);
    }

    @Override
    public void rejectMember(Long memberId) {
        ClubMember member = clubMemberRepository.findById(memberId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        member.setStatus("REJECTED");
        clubMemberRepository.save(member);
    }

    @Override
    public void leaveClub(Long clubId, Long studentId) {
        ClubMember member = clubMemberRepository.findByClubIdAndStudentId(clubId, studentId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        clubMemberRepository.delete(member);
    }
}
