package com.example.restapi_subject.domain.file.service;

import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Value("${file.base-url}")
    private String baseUrl;
    @Value("${file.allowed-ext}")
    private String allowedExtensions;
    @Value("${file.max-size-mb}")
    private int maxSizeMb;

    /** 파일 저장 공통 로직 */
    public String storeFile(MultipartFile file) {

        validateFile(file);

        createUploadDirIfNeeded();

        String ext = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + "." + ext;

        Path savePath = Paths.get(uploadDir, fileName);

        try {
            Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new CustomException(ExceptionType.FILE_SAVE_FAILED);
        }

        return baseUrl + "/" + fileName;
    }

    /** 확장자/크기 검증 */
    private void validateFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new CustomException(ExceptionType.FILE_EMPTY);
        }

        if (file.getSize() > maxSizeMb * 1024 * 1024) {
            throw new CustomException(ExceptionType.FILE_SIZE_EXCEEDED);
        }

        String ext = getFileExtension(file.getOriginalFilename());
        Set<String> allowed = Set.of(allowedExtensions.split(","));

        if (!allowed.contains(ext.toLowerCase())) {
            throw new CustomException(ExceptionType.FILE_EXTENSION_NOT_ALLOWED);
        }
    }

    private String getFileExtension(String filename) {
        String ext = StringUtils.getFilenameExtension(filename);
        if (ext == null) {
            throw new CustomException(ExceptionType.FILE_EXTENSION_NOT_ALLOWED);
        }
        return ext;
    }

    private void createUploadDirIfNeeded() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
