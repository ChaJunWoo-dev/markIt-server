package com.cha.markit.dto.request;

import com.cha.markit.validation.ValidImages;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public abstract class WatermarkRequest {

    @NotNull(message = "이미지를 최소 1개 이상 업로드해주세요")
    @Size(min = 1, message = "이미지를 최소 1개 이상 업로드해주세요")
    @ValidImages
    private List<MultipartFile> images;

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
}
