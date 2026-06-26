# 🏛 Thiết Kế Cơ Sở Dữ Liệu (MySQL) - MyFSchool

Dựa trên yêu cầu dùng Spring Boot (RESTful API), JWT Auth, và tương thích hoàn toàn với giao diện Flutter hiện tại, tôi đề xuất thiết kế Database chuẩn chuẩn hóa (3NF) và linh hoạt cho khả năng mở rộng (như phụ huynh/giáo viên dùng sau này).

## 1. Sơ đồ thực thể ERD & Cấu trúc bảng

### a. Hệ thống User & Authentication (Many-to-Many Roles)
- **`users`**: Chứa thông tin đăng nhập chung.
  - `id` (BIGINT, PK, Auto Increment)
  - `email` (VARCHAR 100, Unique)
  - `password_hash` (VARCHAR 255)
  - `full_name` (VARCHAR 100) - *VD: Phạm Văn Nam*
  - `roll_number` (VARCHAR 20, Unique, Nullable) - *VD: HE180988*
  - `avatar_url` (VARCHAR 255, Nullable)
  - `campus` (VARCHAR 50) - *VD: APHL*
  - `created_at` (TIMESTAMP)
  - `status` (ENUM: 'ACTIVE', 'LOCKED')

- **`roles`**:
  - `id` (INT, PK)
  - `name` (VARCHAR 50, Unique) - *VD: ROLE_STUDENT, ROLE_TEACHER, ROLE_PARENT, ROLE_ADMIN*

- **`user_roles`** (Bảng trung gian n-n):
  - `user_id` (BIGINT, FK)
  - `role_id` (INT, FK)

### b. Quản lý Lớp & Học Kỳ
- **`classes`**:
  - `id` (INT, PK)
  - `name` (VARCHAR 50) - *VD: 12A2*
  - `grade` (INT) - *VD: 12*
  - `academic_year` (VARCHAR 20) - *VD: 2026-2027*

- **`student_classes`** (Học sinh có thể đổi lớp qua các năm, nên dùng bảng trung gian là tốt nhất):
  - `id` (BIGINT, PK)
  - `student_id` (BIGINT, FK -> users)
  - `class_id` (INT, FK -> classes)
  - `is_current` (BOOLEAN) - *Đánh dấu lớp đang học hiện tại*

- **`semesters`**:
  - `id` (INT, PK)
  - `name` (VARCHAR 50) - *VD: Học kỳ 1 - Năm học 2026-2027*
  - `start_date` (DATE)
  - `end_date` (DATE)
  - `is_current` (BOOLEAN)

### c. Bảng Điểm (Grades)
- **`subjects`**:
  - `id` (INT, PK)
  - `code` (VARCHAR 20, Unique)
  - `name` (VARCHAR 100) - *VD: Toán học, Ngữ văn*

- **`grades`**: (Chi tiết điểm)
  - `id` (BIGINT, PK)
  - `student_id` (BIGINT, FK -> users)
  - `subject_id` (INT, FK -> subjects)
  - `semester_id` (INT, FK -> semesters)
  - `midterm_score` (DECIMAL 4,2)
  - `final_score` (DECIMAL 4,2)
  - `average_score` (DECIMAL 4,2)

- **`academic_summaries`**: (Tổng kết kỳ - ĐIỂM GPA)
  - `id` (BIGINT, PK)
  - `student_id` (BIGINT, FK -> users)
  - `semester_id` (INT, FK -> semesters)
  - `gpa` (DECIMAL 4,2) - *VD: 8.6*
  - `academic_performance` (VARCHAR 50) - *VD: Giỏi*
  - `conduct` (VARCHAR 50) - *VD: Tốt (Hạnh kiểm)*

### d. Lịch Học (Schedule)
- **`timetables`**:
  - `id` (BIGINT, PK)
  - `class_id` (INT, FK -> classes)
  - `subject_id` (INT, FK -> subjects)
  - `teacher_id` (BIGINT, FK -> users, Nullable)
  - `day_of_week` (INT) - *2 đến 8 (CN)*
  - `period` (VARCHAR 20) - *VD: Tiết 3-4*
  - `start_time` (TIME) - *08:45:00*
  - `end_time` (TIME) - *10:15:00*
  - `room` (VARCHAR 50) - *VD: Phòng C205*
  - `note` (VARCHAR 255, Nullable) - *VD: Kiểm tra 15P*
  - `is_exam` (BOOLEAN) - *Để highlight chữ màu đỏ*

### e. Đơn Từ (Leave Requests)
- **`leave_requests`**:
  - `id` (BIGINT, PK)
  - `student_id` (BIGINT, FK -> users)
  - `title` (VARCHAR 100) - *VD: Ốm, Việc gia đình*
  - `reason` (TEXT)
  - `start_date` (DATE)
  - `end_date` (DATE)
  - `status` (ENUM: 'PENDING', 'APPROVED', 'REJECTED')
  - `created_at` (TIMESTAMP)

### f. Sự Kiện (Events)
- **`event_categories`**:
  - `id` (INT, PK)
  - `name` (VARCHAR 50) - *VD: Học Thuật, Thể thao*

- **`events`**:
  - `id` (BIGINT, PK)
  - `category_id` (INT, FK -> event_categories)
  - `title` (VARCHAR 200)
  - `start_datetime` (DATETIME)
  - `end_datetime` (DATETIME)
  - `location` (VARCHAR 100) - *VD: Hội trường A*
  - `status` (ENUM: 'UPCOMING', 'ONGOING', 'COMPLETED')

### g. Học Phí (Tuition Fees)
- **`fee_invoices`** (Hóa đơn học phí):
  - `id` (BIGINT, PK)
  - `student_id` (BIGINT, FK -> users)
  - `semester_id` (INT, FK -> semesters)
  - `title` (VARCHAR 150) - *VD: Học phí kỳ Fall 2026*
  - `amount` (DECIMAL 10,2) - *Tổng số tiền cần đóng*
  - `paid_amount` (DECIMAL 10,2) - *Số tiền đã đóng*
  - `due_date` (DATE) - *Hạn nộp*
  - `status` (ENUM: 'UNPAID', 'PARTIAL', 'PAID')
  - `created_at` (TIMESTAMP)

- **`fee_transactions`** (Giao dịch thanh toán):
  - `id` (BIGINT, PK)
  - `fee_invoice_id` (BIGINT, FK -> fee_invoices)
  - `amount` (DECIMAL 10,2)
  - `payment_method` (VARCHAR 50) - *VD: BANK_TRANSFER, VNPAY, CASH*
  - `transaction_date` (DATETIME)
  - `status` (ENUM: 'SUCCESS', 'FAILED', 'PENDING')

### h. Bài Tập Về Nhà (Homework/Assignments)
- **`assignments`** (Bài tập giáo viên giao):
  - `id` (BIGINT, PK)
  - `class_id` (INT, FK -> classes)
  - `subject_id` (INT, FK -> subjects)
  - `teacher_id` (BIGINT, FK -> users)
  - `title` (VARCHAR 200)
  - `description` (TEXT)
  - `due_date` (DATE) - *Ngày cần hoàn thành*
  - `created_at` (TIMESTAMP)

### i. Trò Chuyện (Chat)
- **`conversations`** (Hội thoại):
  - `id` (BIGINT, PK)
  - `name` (VARCHAR 100, Nullable) - *Tên nhóm (VD: Lớp 12A2), NULL nếu là chat 1-1*
  - `type` (ENUM: 'ONE_TO_ONE', 'GROUP')
  - `last_message` (VARCHAR 255)
  - `last_updated` (DATETIME)

- **`conversation_members`** (Thành viên nhóm):
  - `conversation_id` (BIGINT, FK -> conversations)
  - `user_id` (BIGINT, FK -> users)
  - `joined_at` (DATETIME)

- **`messages`** (Tin nhắn):
  - `id` (BIGINT, PK)
  - `conversation_id` (BIGINT, FK -> conversations)
  - `sender_id` (BIGINT, FK -> users)
  - `content` (TEXT)
  - `is_read` (BOOLEAN) - *Đánh dấu trạng thái đọc*
  - `sent_at` (DATETIME)

---

## 2. Lộ Trình Code Backend (Spring Boot 3 + Java 17/21)

Khi bắt đầu code Spring Boot trong thư mục `myfschool_backend`, cấu trúc dự án (Architecture) sẽ theo chuẩn **Layered Architecture (N-Tier)**:

```text
com.fpt.myfschool
 ┣ 📂 config       (Cấu hình Security, Swagger, CORS)
 ┣ 📂 security     (JWT Provider, JwtAuthFilter, UserDetailsServiceImpl)
 ┣ 📂 controller   (Các class @RestController cho Auth, Schedule, Grade, Event...)
 ┣ 📂 service      (Logic nghiệp vụ @Service)
 ┣ 📂 repository   (Spring Data JPA @Repository)
 ┣ 📂 entity       (Các class ánh xạ database @Entity)
 ┣ 📂 dto          (Data Transfer Object - Request/Response: LoginReq, JwtRes, GradeRes...)
 ┗ 📂 exception    (Global Exception Handler @ControllerAdvice)
```

### Các bước triển khai:
1. **Khởi tạo Project**: Spring Web, Spring Data JPA, MySQL Driver, Spring Security, Lombok, Validation.
2. **Cấu hình Database**: Móc nối file `application.yml` tới MySQL.
3. **Module Auth**: Code JWT Token (Access token 1 ngày, Refresh token 30 ngày), luồng đăng nhập (chỉ cần Email + Mật khẩu cho học sinh).
4. **Thiết kế API chi tiết theo từng màn hình (Giao diện Flutter)**:
   Dựa trên các màn hình đã thiết kế, đây là danh sách các API cần thiết:

   **a. Màn hình Login (`login_screen.dart`)**
   - `POST /api/v1/auth/login`: Nhận Email + Password -> Trả về Access Token, Refresh Token và User Info cơ bản.
   - `POST /api/v1/auth/refresh-token`: Cấp lại Access Token mới khi hết hạn.

   **b. Màn hình Trang chủ (`home_screen.dart`) & Profile (`profile_screen.dart`)**
   - `GET /api/v1/users/profile/summary`: Trả về thông tin ngắn gọn cho thẻ Profile ở Home (Tên, Mã SV, Avatar, Tên Lớp, Campus, Trạng thái điểm danh).
   - `GET /api/v1/users/profile/detail`: Trả về thông tin chi tiết cho trang Profile (thêm Email, Cài đặt thông báo...).
   - `PUT /api/v1/users/profile/settings`: (Tùy chọn) Cập nhật cài đặt giao diện/thông báo đẩy.

   **c. Màn hình Bảng Điểm (`grades_screen.dart`)**
   - `GET /api/v1/semesters`: Lấy danh sách các học kỳ (để nạp vào Dropdown chọn kỳ).
   - `GET /api/v1/grades?semesterId={id}`: API tổng hợp trả về 2 phần:
     1. Tổng kết: Điểm GPA, Học lực, Hạnh kiểm.
     2. Chi tiết: Danh sách các môn học kèm điểm Giữa kỳ, Cuối kỳ, Trung bình môn.

   **d. Màn hình Lịch Học (`schedule_screen.dart`)**
   - `GET /api/v1/schedules/next-class`: Trả về đúng 1 Card "Tiết tiếp theo" (Môn, Giờ, Phòng, Tên GV, Avatar GV) dựa theo giờ thực tế hiện tại.
   - `GET /api/v1/schedules?startDate={date}&endDate={date}`: Trả về danh sách thời khóa biểu của cả tuần đó, App sẽ tự nhóm lại theo từng Thứ để hiển thị khi bấm vào các ngày.

   **e. Màn hình Đơn Từ (`leave_request_screen.dart`)**
   - `POST /api/v1/leave-requests`: Nộp đơn xin phép (Gửi Tiêu đề, Lý do, Từ ngày, Đến ngày).
   - `GET /api/v1/leave-requests`: (Dự phòng) Trả về lịch sử các đơn đã nộp và trạng thái (Chờ duyệt, Đã duyệt).

   **f. Màn hình Sự Kiện (`events_screen.dart`)**
   - `GET /api/v1/event-categories`: Lấy danh sách filter (Học Thuật, Thể Thao...).
   - `GET /api/v1/events?status={ONGOING|UPCOMING|COMPLETED}&categoryId={id}`: Lấy danh sách sự kiện theo Tab (Đang diễn ra/Sắp tới) và theo Category filter. Dữ liệu trả về gồm Tiêu đề, Thời gian, Địa điểm, Nhóm danh mục.

   **g. Màn hình Học Phí (`tuition_fees_screen.dart`)**
   - `GET /api/v1/tuition-fees`: Trả về danh sách các hóa đơn học phí của học sinh, bao gồm trạng thái (Chưa nộp, Đã nộp một phần, Đã hoàn thành), số tiền cần nộp, số tiền đã nộp và hạn nộp.
   - `GET /api/v1/tuition-fees/{id}/transactions`: Lấy chi tiết lịch sử giao dịch đóng tiền cho một hóa đơn cụ thể.

   **h. Màn hình Bài Tập Về Nhà (`homework_screen.dart`)**
   - `GET /api/v1/assignments?subjectId={id}&date={date}`: Lấy danh sách bài tập giáo viên giao, hỗ trợ filter theo môn học hoặc thời gian.
   - `GET /api/v1/assignments/{id}`: Xem chi tiết yêu cầu bài tập.

   **i. Màn hình Trò Chuyện (`chat_detail_screen.dart`)**
   - `GET /api/v1/conversations`: Lấy danh sách các cuộc hội thoại gần đây.
   - `GET /api/v1/conversations/{id}/messages`: Lấy lịch sử tin nhắn của một cuộc hội thoại cụ thể.
   - `POST /api/v1/conversations/{id}/messages`: Gửi tin nhắn mới.

---

## 3. PROMPT CHO AI / DEV ĐỂ CODE BACKEND
*Bạn có thể lưu prompt dưới đây để bắt đầu code phần Auth & Entity.*

> "Tôi đang xây dựng một dự án Spring Boot 3 RESTful API. Hệ quản trị CSDL là MySQL. Hãy giúp tôi tạo cấu trúc Security sử dụng JWT Authentication (bao gồm Access Token và Refresh Token). 
> 
> Yêu cầu công việc 1: Viết mã tạo các Entity `@Entity` JPA dựa trên lược đồ cơ sở dữ liệu sau: `User` (có liên kết Many-to-Many với `Role`), `StudentClass` (liên kết User tới Class).
> Yêu cầu công việc 2: Cấu hình `SecurityFilterChain` để vô hiệu hóa CSRF, thiết lập Session Stateless, và mở khóa endpoint `/api/v1/auth/**`, các endpoint khác yêu cầu xác thực.
> Yêu cầu công việc 3: Viết class `JwtTokenProvider` chứa hàm generateToken, validateToken, và lấy userId từ token.
> Hãy chia nhỏ code thành từng file theo kiến trúc chuẩn Controller - Service - Repository."
