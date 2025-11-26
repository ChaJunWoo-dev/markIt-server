package com.cha.markit.service;

import com.cha.markit.domain.Watermark;
import com.cha.markit.dto.request.ImageWatermarkRequest;
import com.cha.markit.dto.request.TextWatermarkRequest;
import com.cha.markit.dto.response.WatermarkListResponse;
import com.cha.markit.dto.response.WatermarkResponse;
import com.cha.markit.exception.BusinessException;
import com.cha.markit.exception.ErrorCode;
import com.cha.markit.repository.WatermarkRepository;
import com.cha.markit.aws.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WatermarkServiceImpl implements WatermarkService {
    private final WatermarkRepository watermarkRepository;
    private final S3Service s3Service;
    private final WatermarkProcessor watermarkProcessor;
    private final WatermarkStorage watermarkStorage;

    @Override
    public byte[] createWatermarkZip(List<MultipartFile> images, TextWatermarkRequest request) throws IOException {
        return watermarkProcessor.createWatermarkZip(images, request);
    }

    @Override
    public byte[] createWatermarkZip(List<MultipartFile> images, ImageWatermarkRequest request) throws IOException {
        return watermarkProcessor.createWatermarkZip(images, request);
    }

    @Override
    public WatermarkResponse saveWatermark(String userId, byte[] zipData, byte[] thumbnailData, int imageCount) throws IOException {
        return watermarkStorage.save(userId, zipData, thumbnailData, imageCount);
    }

    @Override
    public String getDownloadUrl(String watermarkKey, Duration expiration) {
        Watermark watermark = watermarkRepository.findById(watermarkKey)
                .orElseThrow(() -> new BusinessException(ErrorCode.WATERMARK_NOT_FOUND));

        return s3Service.generatePresignedUrl(watermark.getZipKey(), expiration);
    }

    @Override
    public List<WatermarkListResponse> getWatermarkList(String userId) {
        return watermarkRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(watermark -> {
                    String thumbnailUrl = s3Service.generatePresignedUrl(watermark.getThumbnailKey(), Duration.ofHours(1));

                    return WatermarkListResponse.builder()
                            .key(watermark.getKey())
                            .thumbnailUrl(thumbnailUrl)
                            .imageCount(watermark.getImageCount())
                            .createdAt(watermark.getCreatedAt())
                            .build();
                })
                .toList();
    }

    @Override
    public void deleteWatermark(String watermarkKey, String userId) {
        Watermark watermark = watermarkRepository.findById(watermarkKey)
                .orElseThrow(() -> new BusinessException(ErrorCode.WATERMARK_NOT_FOUND));

        if (!watermark.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        try {
            watermarkStorage.delete(watermark);
        } catch (IOException e) {
            log.error("워터마크 삭제 실패 - key: {}, userId: {}", watermarkKey, userId, e);
            throw new BusinessException(ErrorCode.WATERMARK_DELETE_FAILED, e);
        }

        log.info("워터마크 삭제 완료 - key: {}, userId: {}", watermarkKey, userId);
    }
}
