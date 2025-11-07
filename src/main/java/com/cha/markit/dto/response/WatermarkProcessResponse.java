package com.cha.markit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatermarkProcessResponse {

    private String id;
    private int imageCount;
    private LocalDateTime createdAt;
}
