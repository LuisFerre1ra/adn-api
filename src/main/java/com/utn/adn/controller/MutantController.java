package com.utn.adn.controller;

import com.utn.adn.dto.DnaRequest;
import com.utn.adn.dto.StatsResponse;
import com.utn.adn.service.MutantService;
import com.utn.adn.service.StatsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService;

    @Autowired
    public MutantController(MutantService mutant, StatsService stats) {
        mutantService = mutant;
        statsService = stats;
    }

    /**
     * Endpoint principal para detectar si un ADN pertenece a un mutante.
     * Ejemplo de request:
     * {
     *   "dna": ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
     * }
     *
     * @param request DnaRequest con la secuencia de ADN
     * @return 200 OK si es mutante, 403 Forbidden si no lo es
     */
    @PostMapping("/mutant")
    public ResponseEntity<Void> isMutant(@Valid @RequestBody DnaRequest request) {
        boolean isMutant = mutantService.analyzeDna(request.dna());
        return isMutant
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Endpoint para obtener estadísticas globales del sistema.
     * Retorna JSON con el formato:
     * {
     *   "count_mutant_dna": 40,
     *   "count_human_dna": 100,
     *   "ratio": 0.4
     * }
     *
     * @return ResponseEntity con las estadísticas
     */
    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        StatsResponse stats = statsService.getStats();
        return ResponseEntity.ok(stats);
    }
}