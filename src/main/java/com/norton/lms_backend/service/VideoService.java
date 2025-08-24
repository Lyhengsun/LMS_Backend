package com.norton.lms_backend.service;

import com.norton.lms_backend.service.impl.DefaultVideoServiceImpl;
import com.norton.lms_backend.utils.Range;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface VideoService {

    UUID save(MultipartFile video);

    DefaultVideoServiceImpl.ChunkWithMetadata fetchChunk(UUID uuid, Range range);
}
