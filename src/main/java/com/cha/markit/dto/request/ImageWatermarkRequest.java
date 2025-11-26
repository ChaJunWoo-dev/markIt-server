package com.cha.markit.dto.request;

import com.cha.markit.validation.ValidWatermarkImage;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ImageWatermarkRequest extends WatermarkRequest {

    @NotNull(message = "워터마크 이미지는 필수입니다")
    @ValidWatermarkImage
    private MultipartFile watermarkImage;
}
