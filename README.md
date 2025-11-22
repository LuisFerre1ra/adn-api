# ADN API - Detección de Mutantes

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green?style=flat-square&logo=spring)
![Gradle](https://img.shields.io/badge/Gradle-Build-blue?style=flat-square&logo=gradle)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=flat-square&logo=docker)

Este proyecto es una API REST creada en **Java** utilizando **Spring Boot**, diseñada para detectar si un humano es un "mutante" basándose en su secuencia de ADN.

El objetivo principal es analizar una matriz de secuencias de ADN (strings) y buscar patrones específicos (más de una secuencia de 4 letras iguales de forma oblicua, horizontal o vertical).

**🌐 Deploy en producción:** [https://adn-api-4jru.onrender.com/api](https://adn-api-4jru.onrender.com/api)

---

## 🚀 Tecnologías Utilizadas

- **Lenguaje:** Java 21 (o superior)
- **Framework:** Spring Boot 3.x
- **Gestor de Dependencias:** Gradle
- **Contenedorización:** Docker
- **Base de Datos:** H2 (en memoria)
- **Testing:** JUnit 5, Mockito
- **Cobertura de Código:** JaCoCo

---

## ⚙️ Instalación y Ejecución

### Prerrequisitos

- Java JDK 21 o superior
- Gradle (opcional, el proyecto incluye `gradlew`)
- Docker (opcional, para ejecutar en contenedor)

### 1. Clonar el repositorio

```bash
git clone https://github.com/LuisFerre1ra/adn-api.git
cd adn-api
```

### 2. Ejecutar localmente (con Gradle)

```bash
./gradlew bootRun
```

La API estará disponible en `http://localhost:8080`

### 3. Ejecutar con Docker

El proyecto incluye un `Dockerfile` para facilitar su despliegue.

**Construir la imagen:**
```bash
docker build -t adn-api .
```

**Ejecutar el contenedor:**
```bash
docker run -p 8080:8080 adn-api
```

---

## 🔧 Perfiles de Spring

El proyecto cuenta con dos perfiles de configuración:

- **`dev`** (desarrollo): Incluye acceso a la consola H2 para inspección de base de datos
- **`prod`** (producción): Consola H2 desactivada por seguridad

Para ejecutar con un perfil específico:
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

---

## 📡 Endpoints de la API

### 1. Detectar Mutante

Envía una secuencia de ADN para verificar si corresponde a un mutante.

- **URL:** `/api/mutant`
- **Método:** `POST`
- **Content-Type:** `application/json`

**Body (JSON):**
```json
{
    "dna": [
        "ATGCGA",
        "CAGTGC",
        "TTATGT",
        "AGAAGG",
        "CCCCTA",
        "TCACTG"
    ]
}
```

**Respuestas:**
- `200 OK` - Es un mutante
- `403 Forbidden` - Es un humano
- `400 Bad Request` - ADN inválido

### 2. Obtener Estadísticas

Obtiene las estadísticas de las verificaciones de ADN realizadas.

- **URL:** `/api/stats`
- **Método:** `GET`

**Respuesta (JSON):**
```json
{
    "count_mutant_dna": 40,
    "count_human_dna": 100,
    "ratio": 0.4
}
```

---

## 🔗 Enlaces de Interés

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI Docs:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **Consola H2** (solo perfil `dev`): [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
    - JDBC URL: `jdbc:h2:mem:testdb`
    - Usuario: `sa`
    - Password: _(dejar en blanco)_

---

## 🧬 Validación de ADN

Para que una secuencia de ADN sea considerada **válida**, debe cumplir con las siguientes condiciones:

1. **No puede ser nula o vacía**
2. **Debe ser una matriz cuadrada (NxN)**: todas las filas deben tener la misma longitud que el número de filas
3. **Solo puede contener las bases nitrogenadas válidas**: `A`, `T`, `C`, `G` (mayúsculas o minúsculas)

**Ejemplos:**

✅ **ADN Válido:**
```json
{
    "dna": [
        "ATGCGA",
        "CAGTGC",
        "TTATGT",
        "AGAAGG",
        "CCCCTA",
        "TCACTG"
    ]
}
```

❌ **ADN Inválido (no es NxN):**
```json
{
    "dna": [
        "ATGCGA",
        "CAGTGC",
        "TTATGT"
    ]
}
```

❌ **ADN Inválido (contiene caracteres no permitidos):**
```json
{
    "dna": [
        "ATGXGA",
        "CAGTGC",
        "TTATGT",
        "AGAAGG",
        "CCCCTA",
        "TCACTG"
    ]
}
```

---

## 🔍 Lógica de Detección

La API considera que una secuencia de ADN pertenece a un mutante si se encuentran **más de una secuencia de cuatro letras iguales consecutivas** en cualquiera de las siguientes direcciones:

- **Horizontal** → (izquierda a derecha)
- **Vertical** ↓ (arriba hacia abajo)
- **Diagonal principal** ↘ (de esquina superior izquierda a inferior derecha)
- **Diagonal inversa** ↗ (de esquina inferior izquierda a superior derecha)

El algoritmo optimiza la búsqueda deteniéndose inmediatamente al encontrar la segunda secuencia, logrando una complejidad de O(n²) en el mejor caso.

---

## 📂 Estructura del Proyecto

```
com.utn.adn/
├── config/
│   └── SwaggerConfig
├── controller/
│   └── MutantController
├── dto/
│   ├── DnaRequest
│   ├── ErrorResponse
│   └── StatsResponse
├── entity/
│   └── DnaRecord
├── exception/
│   ├── DnaHashCalculationException
│   └── GlobalExceptionHandler
├── repository/
│   └── DnaRecordRepository
├── service/
│   ├── MutantDetector
│   ├── MutantService
│   └── StatsService
└── validation/
    ├── ValidDnaSequence
    └── ValidDnaSequenceValidator
```

---

## 🧪 Tests

El proyecto cuenta con pruebas unitarias e integrales para asegurar la calidad del código y la lógica de detección de mutantes.

**Para ejecutar los tests:**
```bash
./gradlew test
```

**Para generar el reporte de cobertura con JaCoCo:**
```bash
./gradlew jacocoTestReport
```

El reporte se generará en `build/reports/jacoco/test/html/index.html`

### Estructura de Tests

```
test/
└── java/
    └── com.utn.adn/
        ├── controller/
        │   └── MutantControllerTest
        └── service/
            ├── MutantDetectorTest
            ├── MutantServiceTest
            └── StatsServiceTest
```

### Cobertura de Código

El proyecto cuenta con una **cobertura total del 84%** según JaCoCo:

| Paquete | Cobertura de Instrucciones | Cobertura de Ramas |
|---------|---------------------------|-------------------|
| **com.utn.adn.service** | 90% | 91% |
| **com.utn.adn.controller** | 100% | 100% |
| **com.utn.adn.validation** | 94% | 78% |
| **com.utn.adn.dto** | 100% | n/a |
| **com.utn.adn.exception** | 67% | n/a |

**Detalle por clase:**

- `MutantDetector`: 94% de cobertura
- `MutantService`: 79% de cobertura
- `StatsService`: 100% de cobertura
- `MutantController`: 100% de cobertura
- `ValidDnaSequenceValidator`: 94% de cobertura

---

## 📚 Información Académica

Este proyecto fue desarrollado como parte del ejercicio práctico de **Mercado Libre** para la cátedra de **Desarrollo de Software** de la **Universidad Tecnológica Nacional - Facultad Regional Mendoza (UTN FRM)**.

- **Comisión:** 3K9
- **Autor:** Luis Ferreira