package com.qa.practice.products;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.qa.practice.products.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void createAndGetByGtin_shouldReturnCreatedAndFullProduct() throws Exception {
        String body = """
                {
                  "gtin": "7790895000997",
                  "name": "Café Molido 500g",
                  "description": "Café tostado natural",
                  "price": 12.50
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/products/7790895000997")))
                .andExpect(jsonPath("$.gtin", is("7790895000997")))
                .andExpect(jsonPath("$.name", is("Café Molido 500g")))
                .andExpect(jsonPath("$.description", is("Café tostado natural")))
                .andExpect(jsonPath("$.price", is(12.5)));

        mockMvc.perform(get("/api/v1/products/7790895000997"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gtin", is("7790895000997")))
                .andExpect(jsonPath("$.name", is("Café Molido 500g")))
                .andExpect(jsonPath("$.description", is("Café tostado natural")))
                .andExpect(jsonPath("$.price", is(12.5)));
    }

    @Test
    void createDuplicateGtin_shouldReturnConflict() throws Exception {
        String body = """
                {
                  "gtin": "7790895000997",
                  "name": "Café Molido 500g",
                  "description": "Café tostado natural",
                  "price": 12.50
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", containsString("7790895000997")));
    }

    @Test
    void createInvalidProduct_shouldReturnBadRequestWithFieldErrors() throws Exception {
        String invalidBody = """
                {
                  "gtin": "abc",
                  "name": "",
                  "price": -5
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.fieldErrors.gtin").exists())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.price").exists());
    }

    @Test
    void getPaginated_shouldReturnPagedProducts() throws Exception {
        for (int i = 1; i <= 5; i++) {
            String body = String.format("""
                    {
                      "gtin": "779089500000%d",
                      "name": "Producto %d",
                      "description": "Descripción %d",
                      "price": 10.00
                    }
                    """, i, i, i);
            mockMvc.perform(post("/api/v1/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/v1/products?page=0&size=2&sortBy=gtin&sortDir=asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(2)))
                .andExpect(jsonPath("$.totalElements", is(5)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(false)))
                .andExpect(jsonPath("$.content[0].gtin", is("7790895000001")));
    }

    @Test
    void updateProduct_shouldBeIdempotentAndKeepGtinImmutable() throws Exception {
        String createBody = """
                {
                  "gtin": "7790895000997",
                  "name": "Nombre Inicial",
                  "description": "Desc Inicial",
                  "price": 10.00
                }
                """;
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated());

        String updateBody = """
                {
                  "gtin": "9999999999999",
                  "name": "Nombre Actualizado",
                  "description": "Desc Actualizada",
                  "price": 19.99
                }
                """;

        // Primera llamada PUT
        mockMvc.perform(put("/api/v1/products/7790895000997")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gtin", is("7790895000997")))
                .andExpect(jsonPath("$.name", is("Nombre Actualizado")))
                .andExpect(jsonPath("$.description", is("Desc Actualizada")))
                .andExpect(jsonPath("$.price", is(19.99)));

        // Segunda llamada PUT (idempotencia)
        mockMvc.perform(put("/api/v1/products/7790895000997")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gtin", is("7790895000997")))
                .andExpect(jsonPath("$.name", is("Nombre Actualizado")))
                .andExpect(jsonPath("$.price", is(19.99)));
    }

    @Test
    void deleteProduct_shouldReturnNoContentThenNotFound() throws Exception {
        String createBody = """
                {
                  "gtin": "7790895000997",
                  "name": "Producto Temporal",
                  "description": "Para borrar",
                  "price": 5.00
                }
                """;
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/products/7790895000997"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/products/7790895000997"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));

        mockMvc.perform(delete("/api/v1/products/7790895000997"))
                .andExpect(status().isNotFound());
    }

    @Test
    void openApiDocs_shouldBeAccessible() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.paths['/api/v1/products']").exists());
    }
}
