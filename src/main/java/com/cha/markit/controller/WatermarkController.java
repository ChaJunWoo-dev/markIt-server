package com.cha.markit.controller;

import com.cha.markit.dto.WatermarkProcessResponse;
import com.cha.markit.dto.WatermarkRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/watermark")
public class WatermarkController {

    @PostMapping("/process")
    public ResponseEntity<WatermarkProcessResponse> processWatermark(
            @Valid @ModelAttribute WatermarkRequest request
    ) {
        log.info("=== 워터마크 처리 요청 시작 ===");
        log.info("받은 이미지 개수: {}", request.getImages().size());
        log.info("워터마크 설정 - 위치: {}, 크기: {}%, 투명도: {}", 
                request.getConfig().getPosition(), 
                request.getConfig().getSize(), 
                request.getConfig().getOpacity());

        List<String> imageNames = request.getImages().stream()
                .map(MultipartFile::getOriginalFilename)
                .collect(Collectors.toList());

        log.info("=== 이미지 수신 및 검증 완료 ===");

        WatermarkProcessResponse response = WatermarkProcessResponse.builder()
                .message("이미지 " + request.getImages().size() + "개 수신 완료")
                .imageCount(request.getImages().size())
                .imageNames(imageNames)
                .config(request.getConfig())
                .build();

        return ResponseEntity.ok(response);
    }
}
