package com.fpt.myfschool.repository;

import com.fpt.myfschool.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);
    
    List<User> findBySchoolClassId(Integer classId);
    List<User> findByRolesName(String roleName);
    List<User> findBySchoolClassIdAndRolesName(Integer classId, String roleName);
    long countByRolesName(String roleName);

    @org.springframework.data.jpa.repository.Query("SELECT u.schoolClass.grade, COUNT(u) FROM User u JOIN u.roles r WHERE r.name = 'STUDENT' AND u.schoolClass IS NOT NULL GROUP BY u.schoolClass.grade ORDER BY u.schoolClass.grade")
    List<Object[]> countStudentsByGrade();
}
