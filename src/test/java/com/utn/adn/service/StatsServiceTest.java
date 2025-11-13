package com.utn.adn.service;

import com.utn.adn.dto.StatsResponse;
import com.utn.adn.repository.DnaRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private StatsService statsService;

    @Test
    @DisplayName("Debe calcular estadísticas correctamente")
    void testGetStatsWithData() {
        // ARRANGE
        when(dnaRecordRepository.countByMutant(true)).thenReturn(40L);
        when(dnaRecordRepository.countByMutant(false)).thenReturn(100L);

        // ACT
        StatsResponse stats = statsService.getStats();

        // ASSERT
        assertEquals(40, stats.count_mutant_dna());
        assertEquals(100, stats.count_human_dna());
        assertEquals(0.4, stats.ratio(), 0.001);  // 40/100 = 0.4
    }

    @Test
    @DisplayName("Debe retornar ratio 1.0 cuando no hay humanos (solo mutantes)")
    void testGetStatsWithNoHumans() {
        when(dnaRecordRepository.countByMutant(true)).thenReturn(10L);
        when(dnaRecordRepository.countByMutant(false)).thenReturn(0L);

        StatsResponse stats = statsService.getStats();

        assertEquals(10, stats.count_mutant_dna());
        assertEquals(0, stats.count_human_dna());
        assertEquals(1.0, stats.ratio(), 0.001);  // Evita división por cero
    }

    @Test
    @DisplayName("Debe retornar ratio 0 cuando no hay datos")
    void testGetStatsWithNoData() {
        when(dnaRecordRepository.countByMutant(true)).thenReturn(0L);
        when(dnaRecordRepository.countByMutant(false)).thenReturn(0L);

        StatsResponse stats = statsService.getStats();

        assertEquals(0, stats.count_mutant_dna());
        assertEquals(0, stats.count_human_dna());
        assertEquals(0.0, stats.ratio(), 0.001);
    }

    @Test
    @DisplayName("Debe calcular ratio con decimales correctamente")
    void testGetStatsWithDecimalRatio() {
        when(dnaRecordRepository.countByMutant(true)).thenReturn(1L);
        when(dnaRecordRepository.countByMutant(false)).thenReturn(3L);

        StatsResponse stats = statsService.getStats();

        assertEquals(1, stats.count_mutant_dna());
        assertEquals(3, stats.count_human_dna());
        assertEquals(0.333, stats.ratio(), 0.001);  // 1/3 ≈ 0.333
    }

    @Test
    @DisplayName("Debe retornar ratio 1.0 cuando hay igual cantidad")
    void testGetStatsWithEqualCounts() {
        when(dnaRecordRepository.countByMutant(true)).thenReturn(50L);
        when(dnaRecordRepository.countByMutant(false)).thenReturn(50L);

        StatsResponse stats = statsService.getStats();

        assertEquals(50, stats.count_mutant_dna());
        assertEquals(50, stats.count_human_dna());
        assertEquals(1.0, stats.ratio(), 0.001);
    }

    @Test
    @DisplayName("Debe manejar grandes cantidades de datos")
    void testGetStatsWithLargeNumbers() {
        when(dnaRecordRepository.countByMutant(true)).thenReturn(1_000_000L);
        when(dnaRecordRepository.countByMutant(false)).thenReturn(2_000_000L);

        StatsResponse stats = statsService.getStats();

        assertEquals(1_000_000, stats.count_mutant_dna());
        assertEquals(2_000_000, stats.count_human_dna());
        assertEquals(0.5, stats.ratio(), 0.001);  // 1M / 2M = 0.5
    }
}
