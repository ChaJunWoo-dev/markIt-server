package com.cha.markit.controller;

import com.cha.markit.dto.response.DownloadUrlResponse;
import com.cha.markit.service.WatermarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/watermarks")
@RequiredArgsConstructor
public class DownloadController {

    private final WatermarkService watermarkService;

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
}
