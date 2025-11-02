package com.cha.markit.dto.config;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

@Data
@EqualsAndHashCode(callSuper = true)
public class ImageWatermarkConfig extends WatermarkConfig {

    @NotNull(message = "워터마크 이미지는 필수입니다")
    private MultipartFile image;

    @NotNull(message = "워터마크 너비는 필수입니다")
    @Min(value = 10, message = "워터마크 크기는 10 이상이어야 합니다")
    @Max(value = 500, message = "워터마크 크기는 500 이하여야 합니다")
    private Integer width;

    @Override
    public void applyWatermark(BufferedImage image) {
        // todo: 워터마크 처리 로직
    }
}
