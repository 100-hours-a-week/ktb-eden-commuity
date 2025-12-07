package com.example.restapi_subject.domain.file.contorller;

import com.example.restapi_subject.domain.file.service.FileStorageService;
import com.example.restapi_subject.global.common.response.ApiResponse;
import com.example.restapi_subject.global.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileStorageService  fileStorageService;

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProfile(
            @RequestParam("file") MultipartFile file) {
        String url = fileStorageService.storeFile(file);
        return ResponseUtil.ok("profile_upload_success", Map.of("url", url));
    }

    @PostMapping("/board")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadBoard(
            @RequestParam("file") MultipartFile file) {
        String url = fileStorageService.storeFile(file);
        return ResponseUtil.ok("board_upload_success", Map.of("url", url));
    }
}
