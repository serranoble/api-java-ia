package com.qa.practice.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "Datos requeridos para crear un nuevo producto")
public record ProductCreateRequest(
        @Schema(
                description = "Código GTIN único del producto (8 a 14 dígitos numéricos). Es la clave primaria y no podrá modificarse luego.",
                example = "7790895000997"
        )
        @NotBlank(message = "El campo 'gtin' es obligatorio")
        @Pattern(regexp = "^\\d{8,14}$", message = "El 'gtin' debe contener entre 8 y 14 dígitos numéricos")
        String gtin,

        @Schema(description = "Nombre del producto", example = "Café Molido Premium 500g")
        @NotBlank(message = "El campo 'name' es obligatorio")
        @Size(max = 150, message = "El campo 'name' no puede superar los 150 caracteres")
        String name,

        @Schema(description = "Descripción detallada del producto", example = "Café arábica tostado y molido, envase sellado al vacío.")
        @Size(max = 1000, message = "El campo 'description' no puede superar los 1000 caracteres")
        String description,

        @Schema(description = "Precio unitario del producto (mayor a 0, hasta 2 decimales)", example = "12.50")
        @NotNull(message = "El campo 'price' es obligatorio")
        @DecimalMin(value = "0.01", message = "El campo 'price' debe ser mayor o igual a 0.01")
        @Digits(integer = 10, fraction = 2, message = "El campo 'price' debe tener como máximo 10 enteros y 2 decimales")
        BigDecimal price
) {
}
