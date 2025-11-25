package com.example.restapi_subject.domain.user.controller;

import com.example.restapi_subject.global.common.response.ApiResponse;
import com.example.restapi_subject.global.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserUploadController {

    private static final String BASE_UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    /** -----------------------------
     *  프로필 이미지 업로드
     * ----------------------------- */
    @PostMapping("/upload/profile")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProfile(
            @RequestParam("file") MultipartFile file) throws IOException {

        String publicUrl = saveFileAndGetUrl(file);

        return ResponseUtil.ok("profile_upload_success", Map.of("filePath", publicUrl));
    }

    /** -----------------------------
     *  게시글 이미지 업로드
     * ----------------------------- */
    @PostMapping("/upload/board")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadBoardImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        String publicUrl = saveFileAndGetUrl(file);

        return ResponseUtil.ok("board_upload_success", Map.of("filePath", publicUrl));
    }

    /** -----------------------------
     *  내부 메서드 - 파일 저장
     * ----------------------------- */
    private String saveFileAndGetUrl(MultipartFile file) throws IOException {

        File dir = new File(BASE_UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        // 랜덤 파일명 생성
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fullPath = BASE_UPLOAD_DIR + fileName;

        // 파일 저장
        file.transferTo(new File(fullPath));

        // 접근 URL
        return "http://localhost:8080/uploads/" + fileName;
    }
}
