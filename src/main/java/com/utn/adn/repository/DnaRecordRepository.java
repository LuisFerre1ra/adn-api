package com.utn.adn.repository;

import com.utn.adn.entity.DnaRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para acceder a los registros de ADN almacenados.
 * Permite buscar por hash y contar registros según si son mutantes o no.
 */
@Repository
public interface DnaRecordRepository extends JpaRepository<DnaRecord, Long> {

    /**
     * Busca un registro por su hash único de ADN.
     *
     * @param hash hash SHA-256 del ADN
     * @return Optional con el registro, si existe
     */
    Optional<DnaRecord> findByDnaHash(String hash);

    /**
     * Cuenta la cantidad de registros según si son mutantes o no.
     *
     * @param isMutant true para contar mutantes, false para humanos
     * @return número total de registros coincidentes
     */
    long countByMutant(boolean isMutant);
}