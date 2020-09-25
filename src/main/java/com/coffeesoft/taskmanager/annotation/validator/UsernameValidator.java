package com.coffeesoft.taskmanager.annotation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<UsernameConstraint, String> {
    @Override
    public boolean isValid(String usernameField, ConstraintValidatorContext constraintValidatorContext) {
        return usernameField != null && usernameField.length() > 6 && usernameField.length() < 16;
    }
}
