package com.qa.practice.products.controller;

import com.qa.practice.products.dto.ApiError;
import com.qa.practice.products.dto.PagedResponse;
import com.qa.practice.products.dto.ProductCreateRequest;
import com.qa.practice.products.dto.ProductResponse;
import com.qa.practice.products.dto.ProductUpdateRequest;
import com.qa.practice.products.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.net.URI;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/products")
@Validated
@Tag(name = "Products", description = "Operaciones CRUD para la gestión de productos")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "Obtener producto por GTIN",
            description = "Retorna todos los datos del producto identificado por su código GTIN. Operación segura e idempotente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Formato de GTIN inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{gtin}")
    public ResponseEntity<ProductResponse> getByGtin(
            @Parameter(description = "Código GTIN del producto (8 a 14 dígitos)", example = "7790895000997")
            @PathVariable
            @Pattern(regexp = "^\\d{8,14}$", message = "El 'gtin' debe contener entre 8 y 14 dígitos numéricos")
            String gtin) {
        return ResponseEntity.ok(productService.getByGtin(gtin));
    }

    @Operation(
            summary = "Listar productos paginados",
            description = "Retorna una lista paginada con todos los campos de cada producto. Permite configurar página, tamaño y ordenamiento."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado paginado obtenido correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros de paginación u ordenamiento inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getAll(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "El parámetro 'page' debe ser mayor o igual a 0")
            int page,

            @Parameter(description = "Cantidad de elementos por página (entre 1 y 100)", example = "10")
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "El parámetro 'size' debe ser mayor o igual a 1")
            @Max(value = 100, message = "El parámetro 'size' no puede ser mayor a 100")
            int size,

            @Parameter(description = "Campo por el cual ordenar (gtin, name, description, price)", example = "gtin")
            @RequestParam(defaultValue = "gtin")
            String sortBy,

            @Parameter(description = "Dirección del ordenamiento (asc o desc)", example = "asc")
            @RequestParam(defaultValue = "asc")
            @Pattern(regexp = "(?i)^(asc|desc)$", message = "El parámetro 'sortDir' debe ser 'asc' o 'desc'")
            String sortDir) {

        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return ResponseEntity.ok(productService.getAll(pageable));
    }

    @Operation(
            summary = "Crear un nuevo producto",
            description = "Crea un producto nuevo. El GTIN debe ser único; si ya existe retorna 409 Conflict."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Producto creado exitosamente",
                    headers = @Header(name = "Location", description = "URI del recurso creado", schema = @Schema(type = "string")),
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o JSON mal formado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un producto con el GTIN especificado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request) {
        ProductResponse created = productService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{gtin}")
                .buildAndExpand(created.gtin())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(
            summary = "Actualizar un producto existente",
            description = "Actualiza nombre, descripción y precio de un producto existente. El GTIN no es editable. Operación idempotente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o GTIN con formato incorrecto",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{gtin}")
    public ResponseEntity<ProductResponse> update(
            @Parameter(description = "Código GTIN del producto a actualizar (8 a 14 dígitos)", example = "7790895000997")
            @PathVariable
            @Pattern(regexp = "^\\d{8,14}$", message = "El 'gtin' debe contener entre 8 y 14 dígitos numéricos")
            String gtin,
            @Valid @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.update(gtin, request));
    }

    @Operation(
            summary = "Eliminar un producto",
            description = "Elimina el producto identificado por su GTIN. Retorna 204 No Content si se eliminó o 404 Not Found si no existe."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Formato de GTIN inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{gtin}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Código GTIN del producto a eliminar (8 a 14 dígitos)", example = "7790895000997")
            @PathVariable
            @Pattern(regexp = "^\\d{8,14}$", message = "El 'gtin' debe contener entre 8 y 14 dígitos numéricos")
            String gtin) {
        productService.delete(gtin);
        return ResponseEntity.noContent().build();
    }
}
