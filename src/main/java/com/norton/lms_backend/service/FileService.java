package com.norton.lms_backend.service;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.norton.lms_backend.model.dto.response.FileMetaDataResponse;

public interface FileService {
    FileMetaDataResponse uploadFile(MultipartFile file);

    InputStream getFileByFileName(String fileName);
}
