package com.fpt.myfschool.config;
import com.fpt.myfschool.entity.*;
import com.fpt.myfschool.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SemesterRepository semesterRepository;
    private final TimetableRepository timetableRepository;
    private final EventRepository eventRepository;
    private final com.fpt.myfschool.repository.EventCategoryRepository eventCategoryRepository;
    private final GradeRepository gradeRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            log.info("Seeding Roles...");
            Role teacherRole = new Role();
            teacherRole.setName("TEACHER");
            roleRepository.save(teacherRole);

            Role studentRole = new Role();
            studentRole.setName("STUDENT");
            roleRepository.save(studentRole);

            log.info("Seeding Default Users...");
            User teacher = User.builder()
                    .phoneNumber("0987654321")
                    .passwordHash(passwordEncoder.encode("123456"))
                    .fullName("GV. Nguyễn Văn A")
                    .status(User.UserStatus.ACTIVE)
                    .roles(Set.of(teacherRole))
                    .build();
            userRepository.save(teacher);

            User student = User.builder()
                    .phoneNumber("0123456789")
                    .passwordHash(passwordEncoder.encode("123456"))
                    .fullName("HS. Lê Thị B")
                    .status(User.UserStatus.ACTIVE)
                    .roles(Set.of(studentRole))
                    .build();
            userRepository.save(student);

            log.info("Seeding Academic Info...");
            SchoolClass class10A1 = SchoolClass.builder().name("10A1").grade(10).academicYear("2026-2027").build();
            schoolClassRepository.save(class10A1);

            Subject math = Subject.builder().code("MATH10").name("Toán Học").build();
            Subject literature = Subject.builder().code("LIT10").name("Ngữ Văn").build();
            Subject english = Subject.builder().code("ENG10").name("Tiếng Anh").build();
            Subject physics = Subject.builder().code("PHYS10").name("Vật Lý").build();
            Subject chemistry = Subject.builder().code("CHEM10").name("Hóa Học").build();
            Subject biology = Subject.builder().code("BIO10").name("Sinh Học").build();
            Subject history = Subject.builder().code("HIST10").name("Lịch Sử").build();
            Subject geography = Subject.builder().code("GEO10").name("Địa Lý").build();
            Subject pe = Subject.builder().code("PE10").name("Thể Dục").build();
            Subject it = Subject.builder().code("IT10").name("Tin Học").build();

            java.util.List<Subject> allSubjects = java.util.List.of(math, literature, english, physics, chemistry, biology, history, geography, pe, it);
            subjectRepository.saveAll(allSubjects);

            Semester currentSemester = Semester.builder().name("Học kỳ 1").startDate(LocalDate.now().minusMonths(1)).endDate(LocalDate.now().plusMonths(3)).isCurrent(true).build();
            semesterRepository.save(currentSemester);

            log.info("Seeding Timetable...");
            java.util.Random random = new java.util.Random();
            for (int day = 2; day <= 7; day++) { // Thứ 2 đến Thứ 7
                // Sáng: 4 tiết, mỗi tiết 1 môn (tổng cộng 4 môn)
                timetableRepository.save(Timetable.builder().schoolClass(class10A1).subject(allSubjects.get(random.nextInt(allSubjects.size()))).teacher(teacher)
                        .dayOfWeek(day).period("Tiết 1").startTime(LocalTime.of(7, 15)).endTime(LocalTime.of(8, 0)).room("P.101").isExam(false).build());
                timetableRepository.save(Timetable.builder().schoolClass(class10A1).subject(allSubjects.get(random.nextInt(allSubjects.size()))).teacher(teacher)
                        .dayOfWeek(day).period("Tiết 2").startTime(LocalTime.of(8, 5)).endTime(LocalTime.of(8, 50)).room("P.101").isExam(false).build());
                timetableRepository.save(Timetable.builder().schoolClass(class10A1).subject(allSubjects.get(random.nextInt(allSubjects.size()))).teacher(teacher)
                        .dayOfWeek(day).period("Tiết 3").startTime(LocalTime.of(9, 5)).endTime(LocalTime.of(9, 50)).room("P.101").isExam(false).build());
                timetableRepository.save(Timetable.builder().schoolClass(class10A1).subject(allSubjects.get(random.nextInt(allSubjects.size()))).teacher(teacher)
                        .dayOfWeek(day).period("Tiết 4").startTime(LocalTime.of(9, 55)).endTime(LocalTime.of(10, 40)).room("P.101").isExam(false).build());

                // Chiều: 4 tiết (chia làm 2 ca lớn, mỗi ca 2 tiết cho 1 môn)
                timetableRepository.save(Timetable.builder().schoolClass(class10A1).subject(allSubjects.get(random.nextInt(allSubjects.size()))).teacher(teacher)
                        .dayOfWeek(day).period("Tiết 5-6").startTime(LocalTime.of(13, 30)).endTime(LocalTime.of(15, 5)).room("P.102").isExam(false).build());
                timetableRepository.save(Timetable.builder().schoolClass(class10A1).subject(allSubjects.get(random.nextInt(allSubjects.size()))).teacher(teacher)
                        .dayOfWeek(day).period("Tiết 7-8").startTime(LocalTime.of(15, 20)).endTime(LocalTime.of(16, 55)).room("P.102").isExam(false).build());
            }

            log.info("Seeding Grades...");
            for (Subject sub : allSubjects) {
                double mid = 5.0 + (random.nextDouble() * 5.0); // 5.0 to 10.0
                double fin = 5.0 + (random.nextDouble() * 5.0);
                double avg = (mid + fin * 2) / 3;
                gradeRepository.save(Grade.builder().student(student).subject(sub).semester(currentSemester)
                        .midtermScore(Math.round(mid * 10.0) / 10.0)
                        .finalScore(Math.round(fin * 10.0) / 10.0)
                        .averageScore(Math.round(avg * 10.0) / 10.0).build());
            }

            log.info("Seeding Event Categories...");
            com.fpt.myfschool.entity.EventCategory cat1 = eventCategoryRepository.save(com.fpt.myfschool.entity.EventCategory.builder().name("Thể thao").build());
            com.fpt.myfschool.entity.EventCategory cat2 = eventCategoryRepository.save(com.fpt.myfschool.entity.EventCategory.builder().name("Học Thuật").build());
            com.fpt.myfschool.entity.EventCategory cat3 = eventCategoryRepository.save(com.fpt.myfschool.entity.EventCategory.builder().name("Lễ Hội").build());

            log.info("Seeding Events...");
            eventRepository.save(Event.builder().title("Lễ khai giảng năm học mới").category(cat3).startDatetime(LocalDateTime.now().plusDays(2).withHour(8).withMinute(0)).endDatetime(LocalDateTime.now().plusDays(2).withHour(11).withMinute(0)).location("Sân trường chính").status(Event.EventStatus.UPCOMING).build());
            eventRepository.save(Event.builder().title("Hội thi thể thao cấp trường").category(cat1).startDatetime(LocalDateTime.now().plusDays(10).withHour(7).withMinute(0)).endDatetime(LocalDateTime.now().plusDays(12).withHour(17).withMinute(0)).location("Nhà thi đấu").status(Event.EventStatus.UPCOMING).build());
            
            log.info("Database Seeding Completed!");
        }

        // Đảm bảo luôn có ít nhất 5-6 giáo viên để test dropdown
        if (userRepository.findByRolesName("TEACHER").size() < 5) {
            log.info("Seeding More Teachers dynamically...");
            Role teacherRole = roleRepository.findByName("TEACHER").orElse(null);
            if (teacherRole != null) {
                String[] teacherNames = {"Trần Thị C", "Lê Văn D", "Phạm Thu E", "Hoàng Ngọc F", "Đỗ Minh G"};
                for (int i = 0; i < teacherNames.length; i++) {
                    String phone = "098765432" + (i + 2);
                    if (!userRepository.existsByPhoneNumber(phone)) {
                        User t = User.builder()
                                .phoneNumber(phone)
                                .passwordHash(passwordEncoder.encode("123456"))
                                .fullName("GV. " + teacherNames[i])
                                .status(User.UserStatus.ACTIVE)
                                .roles(Set.of(teacherRole))
                                .build();
                        userRepository.save(t);
                    }
                }
                log.info("Added 5 more teachers!");
            }
        }

        if (eventCategoryRepository.count() == 0) {
            log.info("Seeding Event Categories separately...");
            eventCategoryRepository.save(com.fpt.myfschool.entity.EventCategory.builder().name("Thể thao").build());
            eventCategoryRepository.save(com.fpt.myfschool.entity.EventCategory.builder().name("Học thuật").build());
            eventCategoryRepository.save(com.fpt.myfschool.entity.EventCategory.builder().name("Lễ Hội").build());
        }
    }
}