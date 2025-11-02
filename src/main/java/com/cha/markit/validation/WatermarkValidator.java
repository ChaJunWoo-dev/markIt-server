package com.cha.markit.validation;

import com.cha.markit.dto.WatermarkRequest;
import com.cha.markit.dto.WatermarkType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WatermarkValidator implements ConstraintValidator<ValidWatermark, WatermarkRequest> {

    @Override
    public boolean isValid(WatermarkRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getType() == null) {
            return true;
        }

        if (request.getType() == WatermarkType.TEXT) {
            if (request.getTextConfig() == null) {
                setCustomMessage(context, "TEXT 타입일 경우 textConfig는 필수입니다");
                return false;
            }
            if (request.getTextConfig().getText() == null || request.getTextConfig().getText().isBlank()) {
                setCustomMessage(context, "워터마크 텍스트는 필수입니다");
                return false;
            }
        }

        if (request.getType() == WatermarkType.IMAGE) {
            if (request.getImageConfig() == null) {
                setCustomMessage(context, "IMAGE 타입일 경우 imageConfig는 필수입니다");
                return false;
            }
            if (request.getImageConfig().getImage() == null || request.getImageConfig().getImage().isEmpty()) {
                setCustomMessage(context, "워터마크 이미지는 필수입니다");
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
