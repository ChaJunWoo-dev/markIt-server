package com.cha.markit.controller;

import com.cha.markit.dto.response.WatermarkProcessResponse;
import com.cha.markit.dto.request.WatermarkRequest;
import com.cha.markit.service.WatermarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/watermark")
@RequiredArgsConstructor
public class WatermarkController {

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

        List<BufferedImage> watermarkedImages = new ArrayList<>();

        for (MultipartFile imageFile : request.getImages()) {
            BufferedImage result = watermarkService.applyWatermark(imageFile, request.getConfig());
            watermarkedImages.add(result);
        }

        // todo : 최종 응답은 zip파일로 응답 예정
        WatermarkProcessResponse response = WatermarkProcessResponse.builder()
                .message("워터마크 적용 완료")
                .imageCount(request.getImages().size())
                .imageNames(imageNames)
                .config(request.getConfig())
                .build();

        return ResponseEntity.ok(response);
    }
}
