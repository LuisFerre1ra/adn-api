package com.utn.adn.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mutant Detector API")
                        .version("1.0.0")
                        .description("""
                                Esta API permite analizar secuencias de ADN para determinar si pertenecen a un mutante o a un humano.\s
                                                        La verificación se realiza mediante un algoritmo que detecta patrones repetitivos en direcciones\s
                                                        horizontal, vertical y diagonal dentro de una matriz NxN de caracteres (A, T, C, G).""")
                        .contact(new Contact().name("Luis Ferreira").email("lferreiraarg@gmail.com"))
                );
    }
}