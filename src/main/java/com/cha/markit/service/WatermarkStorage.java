package com.cha.markit.service;

import com.cha.markit.domain.Watermark;
import com.cha.markit.dto.response.WatermarkResponse;
import com.cha.markit.repository.WatermarkRepository;
import com.cha.markit.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatermarkStorage {

    private final WatermarkRepository repository;
    private final S3Service s3Service;

    public WatermarkResponse save(String userId, byte[] zipData, int imageCount) throws IOException {
        String key = UUID.randomUUID() + ".zip";
        String eTag = uploadToS3(zipData, key);
        Watermark watermark = saveMetadata(userId, imageCount, key, eTag);

        return WatermarkResponse.builder()
                .id(key)
                .imageCount(imageCount)
                .createdAt(watermark.getCreatedAt())
                .build();
    }

    private String uploadToS3(byte[] data, String key) throws IOException {
        try {
            return s3Service.uploadBytesAsync(key, data).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException("S3 업로드 실패", e);
        }
    }

    private Watermark saveMetadata(String userId, int count, String key, String eTag) {
        Watermark watermark = Watermark.builder()
                .userId(userId)
                .key(key)
                .eTag(eTag)
                .imageCount(count)
                .build();
        repository.save(watermark);

        return watermark;
    }
}
