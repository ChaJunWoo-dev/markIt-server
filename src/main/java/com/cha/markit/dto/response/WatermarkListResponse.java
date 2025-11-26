package com.cha.markit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class WatermarkListResponse {
    private String key;
    private String thumbnailUrl;
    private int imageCount;
    private LocalDateTime createdAt;
}
