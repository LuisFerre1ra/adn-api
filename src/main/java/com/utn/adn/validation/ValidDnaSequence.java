package com.utn.adn.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDnaSequenceValidator.class)
@Documented
public @interface ValidDnaSequence {

    String message() default "Secuencia de ADN inválida. Debe ser NxN y contener solo A, T, C, G.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}