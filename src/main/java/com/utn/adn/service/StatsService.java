package com.utn.adn.service;

import com.utn.adn.dto.StatsResponse;
import com.utn.adn.repository.DnaRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de obtener estadísticas sobre los ADN analizados.
 * Calcula:
 *  - cantidad de ADN mutantes
 *  - cantidad de ADN humanos
 *  - ratio (mutantes / humanos)
 */
@Service
public class StatsService {

    private final DnaRecordRepository dnaRecordRepository;

    @Autowired
    public StatsService(DnaRecordRepository repo) {
        dnaRecordRepository = repo;
    }

    /**
     * Obtiene las estadísticas globales de ADN analizados.
     *
     * @return objeto StatsResponse con counts y ratio
     */
    public StatsResponse getStats() {
        long countMutantDna = dnaRecordRepository.countByMutant(true);
        long countHumanDna = dnaRecordRepository.countByMutant(false);

        double ratio;
        if (countHumanDna == 0 && countMutantDna == 0) {
            ratio = 0.0; // No hay datos aún
        } else if (countHumanDna == 0) {
            ratio = 1.0; // Evita división por cero (solo hay mutantes)
        } else {
            ratio = (double) countMutantDna / countHumanDna;
        }

        return StatsResponse.builder()
                .countMutantDna(countMutantDna)
                .countHumanDna(countHumanDna)
                .ratio(ratio)
                .build();
    }
}