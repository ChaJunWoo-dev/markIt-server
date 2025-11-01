package com.cha.markit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageValidator.class)
@Documented
public @interface ValidImages {
    String message() default "유효하지 않은 이미지입니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
