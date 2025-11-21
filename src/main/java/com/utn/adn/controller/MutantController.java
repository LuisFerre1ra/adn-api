package com.utn.adn.controller;

import com.utn.adn.dto.DnaRequest;
import com.utn.adn.dto.ErrorResponse;
import com.utn.adn.dto.StatsResponse;
import com.utn.adn.service.MutantService;
import com.utn.adn.service.StatsService;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
@Tag(
        name = "Detección de Mutantes",
        description = """
            Controlador principal de la API de análisis de ADN. 
            Permite verificar si una secuencia pertenece a un mutante y 
            consultar estadísticas globales del sistema.
            """
)
@RequiredArgsConstructor
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService;

    // =======================================================
    //                     ENDPOINT /mutant
    // =======================================================

    @Operation(
            summary = "Detecta si un ADN pertenece a un mutante",
            description = """
                Recibe una matriz NxN de ADN compuesta por los caracteres A, T, C y G. 
                El servicio analiza la secuencia en direcciones horizontales, verticales y diagonales 
                para identificar si contiene más de una secuencia mutante.
                Devuelve:
                - 200 OK → Si el ADN corresponde a un mutante  
                - 403 Forbidden → Si pertenece a un humano
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "El ADN es mutante"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "El ADN es humano"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ADN inválido: estructura incorrecta o caracteres no permitidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/mutant")
    public ResponseEntity<Void> isMutant(@Valid @RequestBody DnaRequest request) {
        boolean isMutant = mutantService.analyzeDna(request.dna());
        return isMutant
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // =======================================================
    //                     ENDPOINT /stats
    // =======================================================

    @Operation(
            summary = "Obtiene las estadísticas globales",
            description = """
                Retorna la cantidad de ADN mutante y humano almacenado, junto con el ratio entre ambos.  
                Formato esperado:
                {
                  \"count_mutant_dna\": 40,
                  \"count_human_dna\": 100,
                  \"ratio\": 0.4
                }
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Estadísticas obtenidas correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StatsResponse.class)
                    )
            )
    })
    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        StatsResponse stats = statsService.getStats();
        return ResponseEntity.ok(stats);
    }
}
