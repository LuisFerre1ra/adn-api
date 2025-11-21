package com.utn.adn.service;

import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Clase responsable de analizar el ADN y determinar si pertenece a un mutante.
 * Se considera mutante si existen más de una secuencia de 4 letras iguales consecutivas
 * (en horizontal, vertical o diagonal).
 */
@Service
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;
    private static final int MIN_MUTANT_SEQUENCES = 2;
    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G');

    /**
     * Determina si el ADN pertenece a un mutante.
     * Retorna true si encuentra al menos 2 secuencias de 4 caracteres iguales en:
     *  - Horizontal
     *  - Vertical
     *  - Diagonal principal
     *  - Diagonal inversa
     */
    public boolean isMutant(String[] dna) {
        if (!validateDna(dna)) {
            return false;
        }

        final int n = dna.length;
        final char[][] matrix = convertToMatrix(dna, n);

        int sequencesFound = 0;

        // Recorremos la matriz en un solo loop
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {

                char base = matrix[row][col];

                // Horizontal
                if (col <= n - SEQUENCE_LENGTH &&
                        matrix[row][col + 1] == base &&
                        matrix[row][col + 2] == base &&
                        matrix[row][col + 3] == base) {

                    if (++sequencesFound >= MIN_MUTANT_SEQUENCES) return true;
                }

                // Vertical
                if (row <= n - SEQUENCE_LENGTH &&
                        matrix[row + 1][col] == base &&
                        matrix[row + 2][col] == base &&
                        matrix[row + 3][col] == base) {

                    if (++sequencesFound >= MIN_MUTANT_SEQUENCES) return true;
                }

                // Diagonal principal
                if (row <= n - SEQUENCE_LENGTH && col <= n - SEQUENCE_LENGTH &&
                        matrix[row + 1][col + 1] == base &&
                        matrix[row + 2][col + 2] == base &&
                        matrix[row + 3][col + 3] == base) {

                    if (++sequencesFound >= MIN_MUTANT_SEQUENCES) return true;
                }

                // Diagonal inversa
                if (row <= n - SEQUENCE_LENGTH && col >= SEQUENCE_LENGTH - 1 &&
                        matrix[row + 1][col - 1] == base &&
                        matrix[row + 2][col - 2] == base &&
                        matrix[row + 3][col - 3] == base) {

                    if (++sequencesFound >= MIN_MUTANT_SEQUENCES) return true;
                }
            }
        }

        return false;
    }

    /**
     * Valida que el ADN sea no nulo, NxN y contenga solo A/T/C/G.
     */
    private boolean validateDna(String[] dna) {
        if (dna == null || dna.length == 0) return false;

        int n = dna.length;

        for (String row : dna) {
            if (row == null || row.length() != n) return false;

            for (char c : row.toUpperCase().toCharArray()) {
                if (!VALID_BASES.contains(c)) return false;
            }
        }

        return true;
    }

    /**
     * Convierte el array de Strings en una matriz char[][] optimizada.
     */
    private char[][] convertToMatrix(String[] dna, int n) {
        char[][] matrix = new char[n][n];

        for (int i = 0; i < n; i++) {
            matrix[i] = dna[i].toUpperCase().toCharArray();
        }

        return matrix;
    }
}