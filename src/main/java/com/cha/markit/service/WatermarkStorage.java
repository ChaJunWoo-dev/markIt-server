package com.cha.markit.service;

import com.cha.markit.domain.Watermark;
import com.cha.markit.dto.response.WatermarkResponse;
import com.cha.markit.exception.BusinessException;
import com.cha.markit.exception.ErrorCode;
import com.cha.markit.repository.WatermarkRepository;
import com.cha.markit.aws.S3Service;
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

    public WatermarkResponse save(String userId, byte[] zipData, byte[] thumbnailData, int imageCount) throws IOException {
        String key = UUID.randomUUID().toString();
        String zipKey = key + ".zip";
        String thumbnailKey = key + "_thumb.jpg";

        String zipETag = uploadToS3(zipData, zipKey, "application/zip");
        uploadToS3(thumbnailData, thumbnailKey, "image/jpeg");

        Watermark watermark = saveMetadata(userId, imageCount, key, zipETag);

        return WatermarkResponse.builder()
                .key(key)
                .imageCount(imageCount)
                .createdAt(watermark.getCreatedAt())
                .build();
    }

    private String uploadToS3(byte[] data, String key, String contentType) throws IOException {
        try {
            return s3Service.uploadBytesAsync(key, data, contentType).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED, e);
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

    public void delete(Watermark watermark) throws IOException {
        try {
            s3Service.deleteAsync(watermark.getZipKey())
                    .thenCompose(v -> s3Service.deleteAsync(watermark.getThumbnailKey()))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("S3 파일 삭제 실패 - zipKey: {}, thumbnailKey: {}", 
                    watermark.getZipKey(), watermark.getThumbnailKey(), e);
            throw new BusinessException(ErrorCode.S3_DELETE_FAILED, e);
        }

        repository.delete(watermark);
        log.info("워터마크 스토리지 삭제 완료 - key: {}", watermark.getKey());
    }
}
