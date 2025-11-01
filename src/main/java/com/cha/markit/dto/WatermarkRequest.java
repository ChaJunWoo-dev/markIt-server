package com.cha.markit.dto;

import com.cha.markit.validation.ValidImages;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class WatermarkRequest {

    @NotNull(message = "이미지를 최소 1개 이상 업로드해주세요")
    @Size(min = 1, message = "이미지를 최소 1개 이상 업로드해주세요")
    @ValidImages
    private List<MultipartFile> images;

    @NotNull(message = "워터마크 설정은 필수입니다")
    @Valid
    private WatermarkConfig config;
}
