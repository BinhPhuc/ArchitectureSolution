package com.architecture.solution.annotation;

import com.architecture.solution.annotation.validator.ValueOfEnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueOfEnumValidator.class)
@Documented
public @interface ValueOfEnum {
    Class<? extends Enum<?>> enumClass();

    String message() default "must be valid enum type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
