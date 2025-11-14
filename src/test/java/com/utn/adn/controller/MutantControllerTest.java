package com.utn.adn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utn.adn.dto.DnaRequest;
import com.utn.adn.dto.StatsResponse;
import com.utn.adn.service.MutantService;
import com.utn.adn.service.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MutantController.class)
//@ImportAutoConfiguration(MockitoAutoConfiguration.class)
@Import(MutantControllerTest.MockConfig.class)
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MutantService mutantService;

    @Autowired
    private StatsService statsService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        MutantService mutantService() {
            return Mockito.mock(MutantService.class);
        }

        @Bean
        StatsService statsService() {
            return Mockito.mock(StatsService.class);
        }
    }

    @BeforeEach
    void setup() {
        Mockito.reset(mutantService, statsService);
    }

    @Test
    @DisplayName("POST /mutant debe retornar 200 OK para ADN mutante")
    void testCheckMutantReturns200ForMutant() throws Exception {
        // ARRANGE
        String[] mutantDna = {
                "ATGCGA", "CAGTGC", "TTATGT",
                "AGAAGG", "CCCCTA", "TCACTG"
        };
        DnaRequest request = new DnaRequest(mutantDna);

        when(mutantService.analyzeDna(any(String[].class)))
                .thenReturn(true);  // Mock: es mutante

        // ACT & ASSERT
        mockMvc.perform(
                        post("/api/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());  // 200 OK
    }

    @Test
    @DisplayName("POST /mutant debe retornar 403 Forbidden para ADN humano")
    void testCheckMutantReturns403ForHuman() throws Exception {
        String[] humanDna = {
                "ATGCGA", "CAGTGC", "TTATTT",
                "AGACGG", "GCGTCA", "TCACTG"
        };
        DnaRequest request = new DnaRequest(humanDna);

        when(mutantService.analyzeDna(any(String[].class)))
                .thenReturn(false);  // Mock: es humano

        mockMvc.perform(
                        post("/api/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());  // 403 Forbidden
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 Bad Request para ADN nulo")
    void testCheckMutantReturns400ForNullDna() throws Exception {
        DnaRequest request = new DnaRequest(null);  // DNA nulo

        mockMvc.perform(
                        post("/api/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());  // 400 Bad Request
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 Bad Request para ADN vacío")
    void testCheckMutantReturns400ForEmptyDna() throws Exception {
        DnaRequest request = new DnaRequest(new String[]{});  // Array vacío

        mockMvc.perform(
                        post("/api/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());  // 400 Bad Request
    }

    @Test
    @DisplayName("GET /stats debe retornar estadísticas correctamente")
    void testGetStatsReturnsCorrectData() throws Exception {
        // ARRANGE
        StatsResponse statsResponse = new StatsResponse(40, 100, 0.4);
        when(statsService.getStats()).thenReturn(statsResponse);

        // ACT & ASSERT
        mockMvc.perform(
                        get("/api/stats")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(40))
                .andExpect(jsonPath("$.count_human_dna").value(100))
                .andExpect(jsonPath("$.ratio").value(0.4));
    }

    @Test
    @DisplayName("GET /stats debe retornar 200 OK incluso sin datos")
    void testGetStatsReturns200WithNoData() throws Exception {
        StatsResponse statsResponse = new StatsResponse(0, 0, 0.0);
        when(statsService.getStats()).thenReturn(statsResponse);

        mockMvc.perform(
                        get("/api/stats")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(0))
                .andExpect(jsonPath("$.count_human_dna").value(0))
                .andExpect(jsonPath("$.ratio").value(0.0));
    }

    @Test
    @DisplayName("POST /mutant debe rechazar request sin body")
    void testCheckMutantRejectsEmptyBody() throws Exception {
        mockMvc.perform(
                        post("/api/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                        // NO se incluye .content() → body vacío
                )
                .andExpect(status().isBadRequest());  // 400 Bad Request
    }

    @Test
    @DisplayName("POST /mutant debe aceptar Content-Type application/json")
    void testCheckMutantAcceptsJsonContentType() throws Exception {
        String[] mutantDna = {
                "ATGCGA", "CAGTGC", "TTATGT",
                "AGAAGG", "CCCCTA", "TCACTG"
        };
        DnaRequest request = new DnaRequest(mutantDna);

        when(mutantService.analyzeDna(any(String[].class)))
                .thenReturn(true);

        mockMvc.perform(
                        post("/api/mutant")
                                .contentType(MediaType.APPLICATION_JSON)  // ← Importante
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());
    }
}