package com.example.beQuanTri.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class BirthDateValidator implements ConstraintValidator<BirthDateConstraint, LocalDate> {
    private int min;

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        // Check Null
        if (Objects.isNull(birthDate)) return true;

        // Convert Date to Years
        long years = ChronoUnit.YEARS.between(birthDate, LocalDate.now());

        return years >= min;
    }

    @Override
    public void initialize(BirthDateConstraint constraint) {
        ConstraintValidator.super.initialize(constraint);
        min = constraint.min();
    }
}
