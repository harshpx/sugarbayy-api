package com.store.sugarbayy.annotations.validEmail;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {
    private final EmailValidationHelper emailValidationHelper;
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return emailValidationHelper.isValidEmail(value);
    }
}
