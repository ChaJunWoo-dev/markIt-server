package com.cha.markit.dto.config;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.image.BufferedImage;

@Data
@EqualsAndHashCode(callSuper = true)
public class TextWatermarkConfig extends WatermarkConfig {

    @NotBlank(message = "워터마크 텍스트는 필수입니다")
    @Size(max = 100, message = "워터마크 텍스트는 100자 이하여야 합니다")
    private String text;

    @NotNull(message = "폰트 크기는 필수입니다")
    @Min(value = 10, message = "폰트 크기는 10 이상이어야 합니다")
    @Max(value = 200, message = "폰트 크기는 200 이하여야 합니다")
    private Integer fontSize;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "색상은 HEX 형식이어야 합니다 (예: #FFFFFF)")
    private String color;

    @Override
    public void applyWatermark(BufferedImage image) {
        // todo: 워터마크 처리 로직
    }
}
