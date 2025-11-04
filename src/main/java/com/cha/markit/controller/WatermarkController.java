package com.cha.markit.controller;

import com.cha.markit.dto.response.WatermarkProcessResponse;
import com.cha.markit.dto.request.WatermarkRequest;
import com.cha.markit.service.WatermarkService;
import com.cha.markit.session.SessionManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/watermark")
@RequiredArgsConstructor
public class WatermarkController {

    private final SessionManager sessionManager;
    private final WatermarkService watermarkService;

    @PostMapping("/process")
    public ResponseEntity<WatermarkProcessResponse> processWatermark(
            @Valid @ModelAttribute WatermarkRequest request
    ) throws IOException {
        log.info("=== 워터마크 처리 요청 시작 ===");
        log.info("받은 이미지 개수: {}", request.getImages().size());
        log.info("워터마크 설정 - 위치: {}, 크기: {}%, 투명도: {}",
                request.getConfig().getPosition(), 
                request.getConfig().getSize(), 
                request.getConfig().getOpacity());

        List<String> imageNames = request.getImages().stream()
                .map(MultipartFile::getOriginalFilename)
                .collect(Collectors.toList());

        log.info("=== 이미지 및 워터마크 정보 수신 완료 ===");

        List<byte[]> watermarkedBytes = new ArrayList<>();
        for (MultipartFile imageFile : request.getImages()) {
            BufferedImage watermarked = watermarkService.applyWatermark(imageFile, request.getConfig());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(watermarked, "png", baos);
            watermarkedBytes.add(baos.toByteArray());
        }

        String sessionId = sessionManager.createSession(watermarkedBytes);

        WatermarkProcessResponse response = WatermarkProcessResponse.builder()
                .message("워터마크 처리 완료")
                .sessionId(sessionId)
                .imageCount(request.getImages().size())
                .imageNames(imageNames)
                .config(request.getConfig())
                .build();

        log.info("=== 워터마크 처리 완료: sessionId={} ===", sessionId);

        return ResponseEntity.ok(response);
    }
}
