package com.utn.adn.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ErrorResponse",
        description = "Representa un error estándar devuelto por la API."
)
public record ErrorResponse(

        @Schema(
                description = "Código HTTP asociado al error.",
                example = "400"
        )
        int status,

        @Schema(
                description = "Mensaje descriptivo del error ocurrido.",
                example = "El ADN proporcionado no es válido."
        )
        String message

) {}
