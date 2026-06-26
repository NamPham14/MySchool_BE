package com.fpt.myfschool.controller;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.EventResDto;
import com.fpt.myfschool.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    /**
     * [DÀNH CHO HỌC SINH & GIÁO VIÊN]
     * API hiển thị danh sách Sự kiện sắp tới / Đang diễn ra trên App
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<APIResponse<List<EventResDto>>> getEventsByStatus(@PathVariable String status) {
        List<EventResDto> data = eventService.getEventsByStatus(status);
        return ResponseEntity.ok(APIResponse.<List<EventResDto>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO HỌC SINH & GIÁO VIÊN]
     * API xem nội dung chi tiết của 1 sự kiện
     */
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<EventResDto>> getEventDetail(@PathVariable Long id) {
        EventResDto data = eventService.getEventDetail(id);
        return ResponseEntity.ok(APIResponse.<EventResDto>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * API xem danh sách thể loại sự kiện
     */
    @GetMapping("/categories")
    public ResponseEntity<APIResponse<List<com.fpt.myfschool.entity.EventCategory>>> getAllCategories() {
        List<com.fpt.myfschool.entity.EventCategory> data = eventService.getAllEventCategories();
        return ResponseEntity.ok(APIResponse.<List<com.fpt.myfschool.entity.EventCategory>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN/ADMIN]
     * API xem toàn bộ sự kiện
     */
    @GetMapping
    public ResponseEntity<APIResponse<List<EventResDto>>> getAllEvents() {
        List<EventResDto> data = eventService.getAllEvents();
        return ResponseEntity.ok(APIResponse.<List<EventResDto>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN/ADMIN]
     * API tạo sự kiện mới
     */
    @PostMapping
    public ResponseEntity<APIResponse<EventResDto>> createEvent(@RequestBody com.fpt.myfschool.dto.request.EventReqDto req) {
        EventResDto data = eventService.createEvent(req);
        return ResponseEntity.ok(APIResponse.<EventResDto>builder()
                .status(200).code(1000).message("Tạo thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN/ADMIN]
     * API cập nhật sự kiện
     */
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<EventResDto>> updateEvent(@PathVariable Long id, @RequestBody com.fpt.myfschool.dto.request.EventReqDto req) {
        EventResDto data = eventService.updateEvent(id, req);
        return ResponseEntity.ok(APIResponse.<EventResDto>builder()
                .status(200).code(1000).message("Cập nhật thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN/ADMIN]
     * API xóa sự kiện
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(200).code(1000).message("Xóa thành công").data("OK").build());
    }
}