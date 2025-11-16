package com.cha.markit.validation;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class ImageValidationHelper {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp"
    );
    private static final long MAX_SIZE = 10 * 1024 * 1024;

    public static ValidationResult validate(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return ValidationResult.fail("빈 파일은 업로드할 수 없습니다");
        }

        if (image.getSize() > MAX_SIZE) {
            return ValidationResult.fail(
                String.format("파일 크기가 너무 큽니다 (%s): 최대 10MB까지 업로드 가능합니다",
                    image.getOriginalFilename())
            );
        }

        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            return ValidationResult.fail(
                String.format("지원하지 않는 이미지 형식입니다 (%s): jpg, png, gif, webp만 가능합니다",
                    image.getOriginalFilename())
            );
        }

        return ValidationResult.success();
    }

    public record ValidationResult(boolean valid, String message) {

        public static ValidationResult success() {
                return new ValidationResult(true, null);
            }

        public static ValidationResult fail(String message) {
                return new ValidationResult(false, message);
            }

    }
}
