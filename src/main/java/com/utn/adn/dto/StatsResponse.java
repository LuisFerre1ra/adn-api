package com.utn.adn.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de salida con las estadísticas globales.
 */
@Builder
@Schema(
        name = "StatsResponse",
        description = """
            Representa las estadísticas globales procesadas por la API.
            Incluye la cantidad total de ADN mutante, ADN humano y el ratio entre ambos.
            """
)
public record StatsResponse(

        @Schema(
                description = "Cantidad total de registros de ADN mutante detectados.",
                example = "40"
        )
        @Min(value = 0, message = "El número de ADN mutantes no puede ser negativo")
        long count_mutant_dna,

        @Schema(
                description = "Cantidad total de registros de ADN humano detectados.",
                example = "100"
        )
        @Min(value = 0, message = "El número de ADN humanos no puede ser negativo")
        long count_human_dna,

        @Schema(
                description = "Proporción entre ADN mutante y ADN humano (count_mutant_dna / count_human_dna).",
                example = "0.4"
        )
        @Min(value = 0, message = "El ratio no puede ser negativo")
        double ratio

) { }
