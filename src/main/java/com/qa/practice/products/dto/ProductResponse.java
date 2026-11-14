package com.qa.practice.products.dto;

import com.qa.practice.products.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Representación completa de un producto")
public record ProductResponse(
        @Schema(description = "Código GTIN único del producto (clave primaria)", example = "7790895000997")
        String gtin,

        @Schema(description = "Nombre del producto", example = "Café Molido Premium 500g")
        String name,

        @Schema(description = "Descripción del producto", example = "Café arábica tostado y molido, envase sellado al vacío.")
        String description,

        @Schema(description = "Precio unitario del producto", example = "12.50")
        BigDecimal price
) {
    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
                product.getGtin(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
