package com.utn.adn.dto;

import com.utn.adn.validation.ValidDnaSequence;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public record DnaRequest (

    @NotNull(message = "El campo 'dna' no puede ser nulo.")
    @ValidDnaSequence
    String[] dna

) {}