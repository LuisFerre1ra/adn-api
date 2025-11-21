package com.utn.adn.service;

import com.utn.adn.entity.DnaRecord;
import com.utn.adn.repository.DnaRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // Habilita Mockito
class MutantServiceTest {

    @Mock
    private MutantDetector mutantDetector;  // Mock del detector

    @Mock
    private DnaRecordRepository dnaRecordRepository;  // Mock del repositorio

    @InjectMocks
    private MutantService mutantService;  // Clase bajo prueba

    // ADN de prueba
    private final String[] mutantDna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
    };

    private final String[] humanDna = {
            "ATGCGA",
            "CAGTGC",
            "TTATTT",
            "AGACGG",
            "GCGTCA",
            "TCACTG"
    };

    @Test
    @DisplayName("Debe analizar ADN mutante y guardarlo en DB")
    void testAnalyzeMutantDnaAndSave() {
        // ARRANGE
        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty()); // No existe en BD
        when(mutantDetector.isMutant(mutantDna))
                .thenReturn(true); // Es mutante
        when(dnaRecordRepository.save(any(DnaRecord.class)))
                .thenReturn(new DnaRecord());

        // ACT
        boolean result = mutantService.analyzeDna(mutantDna);

        // ASSERT
        assertTrue(result);

        // VERIFY
        verify(mutantDetector, times(1)).isMutant(mutantDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Debe analizar ADN humano y guardarlo en DB")
    void testAnalyzeHumanDnaAndSave() {
        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty());
        when(mutantDetector.isMutant(humanDna))
                .thenReturn(false);
        when(dnaRecordRepository.save(any(DnaRecord.class)))
                .thenReturn(new DnaRecord());

        boolean result = mutantService.analyzeDna(humanDna);

        assertFalse(result);
        verify(mutantDetector, times(1)).isMutant(humanDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Debe retornar resultado cacheado si el ADN ya fue analizado")
    void testReturnCachedResultForAnalyzedDna() {
        // ARRANGE
        DnaRecord cachedRecord = DnaRecord.builder()
                .id(1L)
                .dnaHash("somehash")
                .mutant(true)
                .build();

        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.of(cachedRecord)); // Ya existe en BD

        // ACT
        boolean result = mutantService.analyzeDna(mutantDna);

        // ASSERT
        assertTrue(result);

        // VERIFY
        verify(mutantDetector, never()).isMutant(any());
        verify(dnaRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe generar hash consistente para el mismo ADN")
    void testConsistentHashGeneration() {
        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty());
        when(mutantDetector.isMutant(any(String[].class)))
                .thenReturn(true);

        mutantService.analyzeDna(mutantDna);
        mutantService.analyzeDna(mutantDna);

        // Debe buscar por el mismo hash ambas veces (mismo valor)
        verify(dnaRecordRepository, times(2)).findByDnaHash(anyString());
    }

    @Test
    @DisplayName("Debe guardar registro con hash correcto")
    void testSavesRecordWithCorrectHash() {
        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty());
        when(mutantDetector.isMutant(mutantDna))
                .thenReturn(true);

        mutantService.analyzeDna(mutantDna);

        verify(dnaRecordRepository).save(argThat(record ->
                record.getDnaHash() != null &&
                        record.getDnaHash().length() == 64 && // SHA-256 = 64 chars hex
                        record.isMutant()
        ));
    }
}
