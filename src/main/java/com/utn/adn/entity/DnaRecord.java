package com.utn.adn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Entidad JPA que representa un registro de ADN analizado.
 * Guarda el hash único del ADN y si es mutante o no
 */
@Entity
@Table(
        name = "dna_records",
        uniqueConstraints = { @UniqueConstraint(columnNames = "dna_hash") },
        indexes = { @Index(name = "idx_dna_hash", columnList = "dna_hash") }
)
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

    /**
     * Fecha y hora en que se registró este ADN.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}