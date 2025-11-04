package com.cha.markit.dto.response;

import com.cha.markit.dto.config.WatermarkConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatermarkProcessResponse {

    private String sessionId;
    private String message;
    private int imageCount;
    private List<String> imageNames;
    private WatermarkConfig config;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
