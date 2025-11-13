package com.utn.adn.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;

/**
 * DTO de salida con las estadísticas globales.
 */
@Builder
public record StatsResponse(

        @Min(value = 0, message = "El número de ADN mutantes no puede ser negativo")
        long count_mutant_dna,

        @Min(value = 0, message = "El número de ADN humanos no puede ser negativo")
        long count_human_dna,

        @Min(value = 0, message = "El ratio no puede ser negativo")
        double ratio

) { }