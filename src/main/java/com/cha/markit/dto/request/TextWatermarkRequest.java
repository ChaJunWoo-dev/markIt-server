package com.cha.markit.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextWatermarkRequest extends WatermarkRequest {

    @NotBlank(message = "워터마크 텍스트는 필수입니다")
    @Size(max = 100, message = "워터마크 텍스트는 100자 이하여야 합니다")
    private String text;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "색상은 HEX 형식이어야 합니다 (예: #FFFFFF)")
    private String color;
}
