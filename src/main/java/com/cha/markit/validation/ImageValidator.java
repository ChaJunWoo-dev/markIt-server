package com.cha.markit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public class ImageValidator implements ConstraintValidator<ValidImages, List<MultipartFile>> {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp"
    );
    private static final long MAX_SIZE = 10 * 1024 * 1024;

    @Override
    public boolean isValid(List<MultipartFile> images, ConstraintValidatorContext context) {
        if (images == null || images.isEmpty()) {
            return false;
        }

        for (MultipartFile image : images) {
            if (image.isEmpty()) {
                setCustomMessage(context, "빈 파일은 업로드할 수 없습니다: " + image.getOriginalFilename());
                return false;
            }

            if (image.getSize() > MAX_SIZE) {
                setCustomMessage(context, String.format("파일 크기가 너무 큽니다 (%s): 최대 10MB까지 업로드 가능합니다",
                        image.getOriginalFilename()));
                return false;
            }

            String contentType = image.getContentType();
            if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
                setCustomMessage(context, String.format("지원하지 않는 이미지 형식입니다 (%s): jpg, png, gif, webp만 가능합니다",
                        image.getOriginalFilename()));
                return false;
            }
        }

        return true;
    }

    private void setCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
