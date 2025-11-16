package com.cha.markit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class WatermarkImageValidator implements ConstraintValidator<ValidWatermarkImage, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile image, ConstraintValidatorContext context) {
        ImageValidationHelper.ValidationResult result = ImageValidationHelper.validate(image);

        if (!result.valid()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(result.message())
                   .addConstraintViolation();

            return false;
        }

        return true;
    }
}
