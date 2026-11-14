package com.qa.practice.products.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Map;

@Schema(description = "Estructura estándar de error de la API")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        @Schema(description = "Fecha y hora del error en formato ISO-8601", example = "2026-11-13T21:20:00-03:00")
        OffsetDateTime timestamp,

        @Schema(description = "Código de estado HTTP", example = "400")
        int status,

        @Schema(description = "Descripción del estado HTTP", example = "Bad Request")
        String error,

        @Schema(description = "Mensaje claro explicando la causa del error", example = "Error de validación en los campos enviados")
        String message,

        @Schema(description = "Ruta del endpoint que generó el error", example = "/api/v1/products")
        String path,

        @Schema(description = "Detalle de errores por campo (solo presente en errores de validación)")
        Map<String, String> fieldErrors
) {
}
