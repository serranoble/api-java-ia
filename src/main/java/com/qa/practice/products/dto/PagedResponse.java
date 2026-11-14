package com.qa.practice.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

@Schema(description = "Respuesta paginada genérica")
public record PagedResponse<T>(
        @Schema(description = "Listado de elementos en la página actual")
        List<T> content,

        @Schema(description = "Número de página actual (inicia en 0)", example = "0")
        int page,

        @Schema(description = "Cantidad máxima de elementos por página", example = "10")
        int size,

        @Schema(description = "Cantidad total de elementos en la base de datos", example = "25")
        long totalElements,

        @Schema(description = "Cantidad total de páginas disponibles", example = "3")
        int totalPages,

        @Schema(description = "Indica si es la primera página", example = "true")
        boolean first,

        @Schema(description = "Indica si es la última página", example = "false")
        boolean last
) {
    public static <T> PagedResponse<T> fromPage(Page<T> pageData) {
        return new PagedResponse<>(
                pageData.getContent(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isFirst(),
                pageData.isLast()
        );
    }
}
