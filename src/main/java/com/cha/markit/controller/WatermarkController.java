package com.cha.markit.controller;

import com.cha.markit.dto.request.WatermarkRequest;
import com.cha.markit.dto.response.WatermarkProcessResponse;
import com.cha.markit.repository.WatermarkRepository;
import com.cha.markit.service.WatermarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RestController
@RequestMapping("/api/watermark")
@RequiredArgsConstructor
public class WatermarkController {

    private final WatermarkService watermarkService;
    private final WatermarkRepository watermarkRepository;

    @PostMapping("/preview")
    public ResponseEntity<byte[]> previewWatermark(
            @Valid @ModelAttribute WatermarkRequest request
    ) throws IOException {
        log.info("=== 워터마크 미리보기 요청 시작 (비로그인) ===");
        log.info("받은 이미지 개수: {}", request.getImages().size());
        log.info("워터마크 설정 - 위치: {}, 크기: {}%, 투명도: {}",
                request.getConfig().getPosition(), 
                request.getConfig().getSize(), 
                request.getConfig().getOpacity());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        for (MultipartFile imageFile : request.getImages()) {
            BufferedImage watermarked = watermarkService.applyWatermark(imageFile, request.getConfig());
            
            ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
            ImageIO.write(watermarked, "png", imageOut);
            
            String filename = imageFile.getOriginalFilename();
            if (filename == null || filename.isEmpty()) {
                filename = "watermarked.png";
            }
            
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(imageOut.toByteArray());
            zos.closeEntry();
        }

        zos.close();

        log.info("=== 워터마크 미리보기 완료: {}개 이미지 ZIP 생성 ===", request.getImages().size());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"watermarks.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(baos.toByteArray());
    }

    @PostMapping("/save")
    public ResponseEntity<WatermarkProcessResponse> createWatermark(
            Authentication auth,
            @Valid @ModelAttribute WatermarkRequest request
    ) throws IOException {
        String userId = auth.getName();
        log.info("=== 워터마크 생성 요청 (로그인) - userId: {} ===", userId);
        log.info("받은 이미지 개수: {}", request.getImages().size());

        byte[] zipData = watermarkService.createWatermarkZip(request.getImages(), request.getConfig());
        WatermarkProcessResponse response = watermarkService.saveWatermark(userId, zipData, request.getImages().size());

        log.info("=== 워터마크 생성 완료 - watermarkId: {} ===", response.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
