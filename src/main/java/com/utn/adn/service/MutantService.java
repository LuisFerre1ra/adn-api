package com.utn.adn.service;

import com.utn.adn.entity.DnaRecord;
import com.utn.adn.repository.DnaRecordRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
@Slf4j
public class MutantService {

    private final MutantDetector mutantDetector;
    private final DnaRecordRepository dnaRecordRepository;

    @Autowired
    public MutantService(MutantDetector md, DnaRecordRepository repo) {
        mutantDetector = md;
        dnaRecordRepository = repo;
    }

    /**
     * Analiza el DNA y retorna true si es mutante.
     * - Calcula hash SHA-256 del array dna (orden y contenido).
     * - Si ya existe en BD, retorna el resultado guardado (cache).
     * - Si no existe, invoca MutantDetector.isMutant(dna), guarda el resultado y lo retorna.
     *
     * @param dna arreglo de Strings representando la matriz NxN
     * @return true si es mutante (>1 secuencia), false en caso contrario
     */
    @Transactional
    public boolean analyzeDna(String[] dna) {
        String hash = calculateDnaHash(dna);
        log.debug("Analizando DNA con hash={}", hash);

        // Buscar en DB (cache)
        Optional<DnaRecord> maybe = dnaRecordRepository.findByDnaHash(hash);
        if (maybe.isPresent()) {
            boolean cached = maybe.get().isMutant();
            log.debug("Resultado cacheado encontrado: isMutant={} for hash={}", cached, hash);
            return cached;
        }

        // No está en BD => analizar
        boolean isMutant = false;
        try {
            isMutant = mutantDetector.isMutant(dna);
        } catch (Exception e) {
            log.error("Error al ejecutar MutantDetector", e);
            // Decide si rethrow o retornar false; aquí retornamos false y guardamos el resultado como humano
            isMutant = false;
        }

        // Guardar resultado (evitar duplicados por unique constraint en dna_hash)
        DnaRecord record = new DnaRecord();
        record.setDnaHash(hash);
        record.setMutant(isMutant);

        try {
            dnaRecordRepository.save(record);
            log.debug("Registro guardado para hash={}, isMutant={}", hash, isMutant);
        } catch (Exception e) {
            // Si falla el save por concurrencia/unique constraint, loguear y continuar.
            log.warn("No se pudo guardar DnaRecord (probable duplicado) para hash={}: {}", hash, e.getMessage());
        }

        return isMutant;
    }

    /**
     * Calcula SHA-256 del DNA. Concatenamos las filas con un separador consistente para
     * garantizar que la misma matriz produzca siempre el mismo hash.
     *
     * @param dna arreglo de Strings
     * @return string hex de 64 chars (SHA-256)
     */
    private String calculateDnaHash(String[] dna) {
        if (dna == null) return sha256Hex("null");

        // Concatenar con separador inmutable (por ejemplo '|')
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dna.length; i++) {
            if (i > 0) sb.append('|');
            sb.append(dna[i]);
        }
        return sha256Hex(sb.toString());
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            // convertir a hex
            StringBuilder hex = new StringBuilder(2 * digest.length);
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 siempre disponible en JVM estándar, pero por si acaso:
            log.error("SHA-256 algorithm not available", e);
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}