package com.fpt.myfschool.repository;

import com.fpt.myfschool.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
    List<ClubMember> findByClubId(Long clubId);
    List<ClubMember> findByStudentId(Long studentId);
    Optional<ClubMember> findByClubIdAndStudentId(Long clubId, Long studentId);
    List<ClubMember> findByClubIdAndStatus(Long clubId, String status);
}
