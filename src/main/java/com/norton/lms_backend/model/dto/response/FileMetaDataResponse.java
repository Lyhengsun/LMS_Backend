package com.norton.lms_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileMetaDataResponse {
    private String fileName;
    private String fileType;
    private String fileUrl;
    private Long fileSize;
}
