package com.utn.adn.service;

import com.utn.adn.validation.ValidDnaSequenceValidator;
import org.springframework.stereotype.Service;

/**
 * Clase responsable de analizar el ADN y determinar si pertenece a un mutante.
 * Se considera mutante si existen más de una secuencia de 4 letras iguales consecutivas
 * (en horizontal, vertical o diagonal).
 */
@Service
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;
    private static final int MIN_MUTANT_SEQUENCES = 2;

    /**
     * Determina si el ADN pertenece a un mutante.
     *
     * @param dna arreglo NxN de cadenas que representan las bases nitrogenadas
     * @return true si es mutante, false si es humano
     * @throws IllegalArgumentException si el ADN es nulo, vacío, no cuadrado o contiene caracteres inválidos
     */
    public boolean isMutant(String[] dna) {
        if(!validateDna(dna)) return false;

        int n = dna.length;
        char[][] matrix = new char[n][n];

        // Copiar ADN a matriz
        for (int i = 0; i < n; i++) {
            matrix[i] = dna[i].toUpperCase().toCharArray();
        }

        int found = 0;

        // Recorrer toda la matriz buscando secuencias
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                char base = matrix[row][col];
                if (isSequence(matrix, row, col, 0, 1, base)) found++;   // derecha
                if (isSequence(matrix, row, col, 1, 0, base)) found++;   // abajo
                if (isSequence(matrix, row, col, 1, 1, base)) found++;   // diagonal principal
                if (isSequence(matrix, row, col, 1, -1, base)) found++;  // antidiagonal

                if (found >= MIN_MUTANT_SEQUENCES)
                    return true;
            }
        }

        return false;
    }

    /**
     * Verifica si desde (row, col) hay una secuencia válida en la dirección dada.
     */
    private boolean isSequence(char[][] matrix, int row, int col, int dRow, int dCol, char base) {
        int n = matrix.length;

        int endRow = row + (SEQUENCE_LENGTH - 1) * dRow;
        int endCol = col + (SEQUENCE_LENGTH - 1) * dCol;

        // Si no hay espacio suficiente
        if (endRow < 0 || endRow >= n || endCol < 0 || endCol >= n)
            return false;

        for (int i = 1; i < SEQUENCE_LENGTH; i++) {
            if (matrix[row + i * dRow][col + i * dCol] != base)
                return false;
        }

        return true;
    }

    /**
     * Valida que el ADN sea no nulo, no vacío, cuadrado NxN y con caracteres válidos.
     *
     * @param dna arreglo de cadenas
     */
    private boolean validateDna(String[] dna) {
        if (dna == null || dna.length == 0)
            return false;

        int n = dna.length;
        for (String row : dna) {
            if (row == null || row.isEmpty())
                return false;

            if (row.length() != n)
                return false;

            for (char c : row.toUpperCase().toCharArray()) {
                if ("ATCG".indexOf(c) == -1)
                    return false;
            }
        }

        return true;
    }
}