package com.fpt.myfschool.controller;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;

    /**
     * [DÀNH CHO GIÁO VIÊN & HỌC SINH]
     * API upload một file ảnh (cho Avatar, cho Bài tập) lên Cloudinary và trả về URL ảnh.
     */
    @PostMapping
    public ResponseEntity<APIResponse<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Map uploadResult = cloudinaryService.uploadFile(file);
            String imageUrl = uploadResult.get("secure_url").toString();
            return ResponseEntity.ok(APIResponse.<String>builder()
                    .status(200).code(1000).message("Upload thành công").data(imageUrl).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(APIResponse.<String>builder()
                    .status(400).code(9999).message("Upload thất bại: " + e.getMessage()).build());
        }
    }
}