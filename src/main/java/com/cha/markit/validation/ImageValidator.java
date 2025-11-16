package com.cha.markit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ImageValidator implements ConstraintValidator<ValidImages, List<MultipartFile>> {

    @Override
    public boolean isValid(List<MultipartFile> images, ConstraintValidatorContext context) {
        if (images == null || images.isEmpty()) {
            return false;
        }

        for (MultipartFile image : images) {
            ImageValidationHelper.ValidationResult result = ImageValidationHelper.validate(image);

            if (!result.valid()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(result.message())
                       .addConstraintViolation();

                return false;
            }
        }

        return true;
    }
}
