package com.store.sugarbayy.annotations.validEmail;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {
    String message() default "Invalid/Empty/Disposable email addresses are not allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
