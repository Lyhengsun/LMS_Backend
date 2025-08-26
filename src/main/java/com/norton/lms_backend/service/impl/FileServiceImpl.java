package com.norton.lms_backend.service.impl;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import com.norton.lms_backend.exception.InvalidException;
import com.norton.lms_backend.model.dto.response.FileMetaDataResponse;
import com.norton.lms_backend.model.entity.FileMetadataEntity;
import com.norton.lms_backend.repository.FileMetadataRepository;
import com.norton.lms_backend.service.FileService;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final MinioClient minioClient;
    private final FileMetadataRepository fileMetadataRepository;

    @Value("${minio.bucket.name}")
    private String bucketName;

    private boolean verifyImage(MultipartFile file) {
        // validate file extension allow only ending with .png, .svg, .jpg, .jpeg, or
        // .gif
        List<String> allowFileExtensions = List.of("image/png", "image/svg+xml", "image/jpg", "image/jpeg",
                "image/gif");
        return allowFileExtensions.contains(file.getContentType());
    }

    private boolean verifyVideo(MultipartFile file) {
        // validate file extension allow only ending with .mp4, or .mkv
        List<String> allowFileExtensions = List.of("video/mp4", "video/x-matroska");
        return allowFileExtensions.contains(file.getContentType());
    }

    private boolean verifyExtraExtension(MultipartFile file) {
        // validate file extension allow only ending with .pdf, .docx or .zip
        List<String> allowFileExtensions = List.of("application/pdf",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/zip",
                "application/x-zip-compressed");
        return allowFileExtensions.contains(file.getContentType());
    }

    private void verifyFileExtension(MultipartFile file) {
        if (!(verifyImage(file) || verifyVideo(file) || verifyExtraExtension(file)) || file.getContentType() == null) {
            throw new InvalidException(
                    "File must be a valid file ending with (.png .svg .jpg .jpeg .gif) for image, (.mp4, .mkv) for video and (.pdf, .docx .zip) for document");
        }
    }

    @SneakyThrows
    private FileMetaDataResponse uploadFileToMinio(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(fileName);

        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName)
                .contentType(file.getContentType()).stream(file.getInputStream(), file.getSize(), -1)
                .build());

        FileMetadataEntity metadata = FileMetadataEntity.builder()
                .id(fileName.toString())
                .size(file.getSize())
                .httpContentType(file.getContentType())
                .build();
        fileMetadataRepository.save(metadata);

        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/files/preview-file/" + fileName).toUriString();

        return FileMetaDataResponse.builder().fileName(fileName).fileType(file.getContentType())
                .fileUrl(fileUrl).fileSize(file.getSize()).build();
    }

    @SneakyThrows
    @Override
    public FileMetaDataResponse uploadFile(MultipartFile file) {
        verifyFileExtension(file);

        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());

        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        return uploadFileToMinio(file);
    }

    @SneakyThrows
    @Override
    public InputStream getFileByFileName(String fileName) {

        return minioClient
                .getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
    }

}
