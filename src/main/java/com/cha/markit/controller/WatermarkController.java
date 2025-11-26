package com.cha.markit.controller;

import com.cha.markit.dto.request.ImageWatermarkRequest;
import com.cha.markit.dto.request.TextWatermarkRequest;
import com.cha.markit.dto.response.DownloadUrlResponse;
import com.cha.markit.dto.response.WatermarkListResponse;
import com.cha.markit.dto.response.WatermarkResponse;
import com.cha.markit.service.WatermarkProcessor;
import com.cha.markit.service.WatermarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/watermarks")
@RequiredArgsConstructor
public class WatermarkController {

    private final WatermarkService watermarkService;
    private final WatermarkProcessor watermarkProcessor;

    @PostMapping("/preview/text")
    public ResponseEntity<byte[]> previewTextWatermark(
            @Valid @ModelAttribute TextWatermarkRequest request
    ) throws IOException {
        log.info("=== 텍스트 워터마크 미리보기 요청 시작 (비로그인) ===");

        byte[] zipData = watermarkService.createWatermarkZip(request.getImages(), request);

        log.info("=== 텍스트 워터마크 미리보기 완료: {}개 이미지 ZIP 생성 ===", request.getImages().size());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"watermarks.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipData);
    }

    @PostMapping("/preview/image")
    public ResponseEntity<byte[]> previewImageWatermark(
            @Valid @ModelAttribute ImageWatermarkRequest request
    ) throws IOException {
        log.info("=== 이미지 워터마크 미리보기 요청 시작 (비로그인) ===");

        byte[] zipData = watermarkService.createWatermarkZip(request.getImages(), request);

        log.info("=== 이미지 워터마크 미리보기 완료: {}개 이미지 ZIP 생성 ===", request.getImages().size());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"watermarks.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipData);
    }

    @PostMapping("/text")
    public ResponseEntity<WatermarkResponse> createTextWatermark(
            @AuthenticationPrincipal String userId,
            @Valid @ModelAttribute TextWatermarkRequest request
    ) throws IOException {
        log.info("=== 텍스트 워터마크 생성 요청 (로그인) - userId: {} ===", userId);

        byte[] zipData = watermarkService.createWatermarkZip(request.getImages(), request);
        byte[] thumbnailData = createThumbnail(request);
        WatermarkResponse response = watermarkService.saveWatermark(userId, zipData, thumbnailData, request.getImages().size());

        log.info("=== 텍스트 워터마크 생성 완료 - watermarkId: {} ===", response.getKey());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/image")
    public ResponseEntity<WatermarkResponse> createImageWatermark(
            @AuthenticationPrincipal String userId,
            @Valid @ModelAttribute ImageWatermarkRequest request
    ) throws IOException {
        log.info("=== 이미지 워터마크 생성 요청 (로그인) - userId: {} ===", userId);

        byte[] zipData = watermarkService.createWatermarkZip(request.getImages(), request);
        byte[] thumbnailData = createThumbnail(request);
        WatermarkResponse response = watermarkService.saveWatermark(userId, zipData, thumbnailData, request.getImages().size());

        log.info("=== 이미지 워터마크 생성 완료 - watermarkId: {} ===", response.getKey());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<WatermarkListResponse>> getWatermarkList(@AuthenticationPrincipal String userId) {
        List<WatermarkListResponse> watermarks = watermarkService.getWatermarkList(userId);

        return ResponseEntity.ok(watermarks);
    }

    @GetMapping("/{watermarkKey}/download")
    public ResponseEntity<DownloadUrlResponse> getDownloadUrl(@PathVariable String watermarkKey) {
        Duration expiration = Duration.ofMinutes(30);
        String downloadUrl = watermarkService.getDownloadUrl(watermarkKey, expiration);

        DownloadUrlResponse response = DownloadUrlResponse.builder()
                .downloadUrl(downloadUrl)
                .expiresInSeconds(expiration.getSeconds())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteWatermark(
            @PathVariable("key") String watermarkKey,
            @AuthenticationPrincipal String userId
    ) {
        log.info("=== 워터마크 삭제 요청 - key: {}, userId: {} ===", watermarkKey, userId);

        watermarkService.deleteWatermark(watermarkKey, userId);

        log.info("=== 워터마크 삭제 완료 - key: {} ===", watermarkKey);
        return ResponseEntity.noContent().build();
    }

    private byte[] createThumbnail(TextWatermarkRequest request) throws IOException {
        return watermarkProcessor.createThumbnail(request.getImages(), request);
    }

    private byte[] createThumbnail(ImageWatermarkRequest request) throws IOException {
        return watermarkProcessor.createThumbnail(request.getImages(), request);
    }
}

