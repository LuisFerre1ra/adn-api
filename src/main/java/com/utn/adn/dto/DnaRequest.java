package com.utn.adn.dto;

import com.utn.adn.validation.ValidDnaSequence;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "DnaRequest",
        description = """
            Representa la solicitud enviada al endpoint /mutant. 
            Contiene una secuencia de ADN expresada como un arreglo de cadenas NxN, 
            donde cada cadena debe estar compuesta únicamente por los caracteres A, T, C o G.
            """
)
public record DnaRequest(

        @Schema(
                description = "Matriz de ADN (NxN) donde cada valor debe contener solo A, T, C o G.",
                example = "[\"ATGCGA\", \"CAGTGC\", \"TTATGT\", \"AGAAGG\", \"CCCCTA\", \"TCACTG\"]",
                required = true
        )
        @NotNull(message = "El campo 'dna' no puede ser nulo.")
        @ValidDnaSequence
        String[] dna

) {}
