package com.utn.adn.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class ValidDnaSequenceValidator implements ConstraintValidator<ValidDnaSequence, String[]> {

    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G');

    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        if (dna == null || dna.length == 0) {
            return false;
        }

        int n = dna.length;

        // Verificar que todas las cadenas tengan longitud n
        for (String row : dna) {
            if (row == null || row.length() != n) {
                return false;
            }

            // Verificar que todos los caracteres sean A, T, C o G
            for (char c : row.toCharArray()) {
                if (!VALID_BASES.contains(c)) {
                    return false;
                }
            }
        }

        return true;
    }
}