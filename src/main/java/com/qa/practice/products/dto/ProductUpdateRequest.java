package com.qa.practice.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "Datos editables para actualizar un producto existente. El GTIN no se incluye porque es inmutable.")
public record ProductUpdateRequest(
        @Schema(description = "Nombre actualizado del producto", example = "Café Molido Especial 500g")
        @NotBlank(message = "El campo 'name' es obligatorio")
        @Size(max = 150, message = "El campo 'name' no puede superar los 150 caracteres")
        String name,

        @Schema(description = "Descripción actualizada del producto", example = "Nueva cosecha 100% arábica de altura.")
        @Size(max = 1000, message = "El campo 'description' no puede superar los 1000 caracteres")
        String description,

        @Schema(description = "Precio unitario actualizado (mayor a 0, hasta 2 decimales)", example = "14.90")
        @NotNull(message = "El campo 'price' es obligatorio")
        @DecimalMin(value = "0.01", message = "El campo 'price' debe ser mayor o igual a 0.01")
        @Digits(integer = 10, fraction = 2, message = "El campo 'price' debe tener como máximo 10 enteros y 2 decimales")
        BigDecimal price
) {
}
