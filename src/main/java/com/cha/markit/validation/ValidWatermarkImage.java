package com.cha.markit.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WatermarkImageValidator.class)
public @interface ValidWatermarkImage {
    String message() default "유효하지 않은 워터마크 이미지입니다";
}
