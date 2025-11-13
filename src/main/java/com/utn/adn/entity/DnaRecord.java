package com.utn.adn.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad JPA que representa un registro de ADN analizado.
 * Guarda el hash único del ADN y si es mutante o no
 */
@Entity
@Table(name = "dna_records", uniqueConstraints = {
        @UniqueConstraint(columnNames = "dna_hash")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DnaRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Hash SHA-256 del ADN (único).
     */
    @Column(name = "dna_hash", nullable = false, unique = true, length = 64)
    private String dnaHash;

    /**
     * Indica si el ADN corresponde a un mutante.
     */
    @Column(name = "is_mutant", nullable = false)
    private boolean mutant;
}