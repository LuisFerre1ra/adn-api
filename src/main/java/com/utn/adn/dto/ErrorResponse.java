package com.utn.adn.dto;

public record ErrorResponse(
        int status,
        String message
) {}