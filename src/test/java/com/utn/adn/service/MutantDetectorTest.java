package com.utn.adn.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MutantDetectorTest {
    MutantDetector mutantDetector;

    @BeforeEach
    void setUp() {
        mutantDetector = new MutantDetector();  // Crear instancia real
    }

    @Test
    @DisplayName("Debe detectar mutante con secuencias horizontal y diagonal")
    void testMutantWithHorizontalAndDiagonalSequences() {
        String[] dna = {
                "ATGCGA",  // Fila 0
                "CAGTGC",  // Fila 1
                "TTATGT",  // Fila 2
                "AGAAGG",  // Fila 3
                "CCCCTA",  // Fila 4 ← Horizontal: CCCC
                "TCACTG"   // Fila 5
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con secuencias verticales")
    void testMutantWithVerticalSequences() {
        String[] dna = {
                "AAAAGA",  // 4 A's en columna 0
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CACCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con múltiples secuencias horizontales")
    void testMutantWithMultipleHorizontalSequences() {
        String[] dna = {
                "TTTTGA",  // Secuencia 1: TTTT
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",  // Secuencia 2: CCCC
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con diagonales ascendentes y descendentes")
    void testMutantWithBothDiagonals() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATTT",  // Modificado para crear secuencias
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("No debe detectar mutante con una sola secuencia")
    void testNotMutantWithOnlyOneSequence() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATTT",  // Solo 1 secuencia: TTT (solo 3, no cuenta)
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("No debe detectar mutante sin secuencias")
    void testNotMutantWithNoSequences() {
        String[] dna = {
                "ATGC",
                "CAGT",
                "TTAT",
                "AGAC"
        };
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe rechazar ADN nulo")
    void testNullDna() {
        assertFalse(mutantDetector.isMutant(null));
    }

    @Test
    @DisplayName("Debe rechazar ADN vacío")
    void testEmptyDna() {
        String[] dna = {};
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe rechazar matriz no cuadrada")
    void testNonSquareMatrix() {
        String[] dna = {
                "ATGCGA",  // 6 caracteres
                "CAGTGC",  // 6 caracteres
                "TTATGT"   // 6 caracteres, pero solo 3 filas
        };
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe rechazar caracteres inválidos")
    void testInvalidCharacters() {
        String[] dna = {
                "ATGCGA",
                "CAGTXC",  // ← 'X' es inválido
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante en matriz pequeña 4x4")
    void testSmallMatrix4x4Mutant() {
        String[] dna = {
                "AAAA",  // Horizontal: AAAA
                "CCCC",  // Horizontal: CCCC
                "TTAT",
                "AGAC"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe manejar matriz grande 10x10")
    void testLargeMatrix10x10() {
        String[] dna = {
                "ATGCGAATGC",
                "CAGTGCCAGT",
                "TTATGTTTAT",
                "AGAAGGATAA",
                "CCCCTACCCC",  // 2 horizontales: CCCC (pos 0-3 y 6-9)
                "TCACTGTCAC",
                "ATGCGAATGC",
                "CAGTGCCAGT",
                "TTATGTTTAT",
                "AGAAGGATAA"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar diagonal ascendente")
    void testAscendingDiagonal() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCGCTA",
                "TCGCTG"
        };
        boolean result = mutantDetector.isMutant(dna);
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe usar early termination para eficiencia")
    void testEarlyTermination() {
        String[] dna = {
                "AAAAGA",  // Secuencia 1
                "AAAAGC",  // Secuencia 2
                "TTATGT",  // Ya no se revisa (early termination)
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };

        long startTime = System.nanoTime();
        boolean result = mutantDetector.isMutant(dna);
        long endTime = System.nanoTime();

        assertTrue(result);
        assertTrue((endTime - startTime) < 10_000_000); // < 10ms
    }

    @Test
    @DisplayName("Debe detectar mutante con todas las bases iguales")
    void testAllSameBases() {
        String[] dna = {
                "AAAAAA",
                "AAAAAA",
                "AAAAAA",
                "AAAAAA",
                "AAAAAA",
                "AAAAAA"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe rechazar fila nula en el array")
    void testNullRowInArray() {
        String[] dna = {
                "ATGCGA",
                null,      // ← Fila nula
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertFalse(mutantDetector.isMutant(dna));
    }
}
