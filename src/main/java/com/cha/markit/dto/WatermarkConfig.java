package com.cha.markit.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.image.BufferedImage;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public abstract class WatermarkConfig {

    @NotBlank(message = "워터마크 위치는 필수입니다")
    @Pattern(regexp = "TOP_LEFT|TOP_CENTER|TOP_RIGHT|CENTER_LEFT|CENTER|CENTER_RIGHT|BOTTOM_LEFT|BOTTOM_CENTER|BOTTOM_RIGHT",
            message = "잘못된 워터마크 위치입니다")
    private String position;

    @Min(value = 1, message = "크기는 1 이상이어야 합니다")
    @Max(value = 100, message = "크기는 100 이하여야 합니다")
    private int size;

    @DecimalMin(value = "0.0", message = "투명도는 0 이상이어야 합니다")
    @DecimalMax(value = "1.0", message = "투명도는 1 이하여야 합니다")
    private float opacity;

    public abstract void applyWatermark(BufferedImage image);
}
