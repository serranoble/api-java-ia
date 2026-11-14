package com.qa.practice.products.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Products CRUD API - QA Automation Practice")
                        .version("1.0.0")
                        .description("""
                                API REST de gestión de productos diseñada para práctica de automatización de pruebas (QA).
                                
                                **Reglas principales:**
                                - **Clave primaria (`gtin`)**: Identificador único de 8 a 14 dígitos numéricos. No es editable una vez creado.
                                - **Campos editables**: `name`, `description` y `price`.
                                - **Idempotencia**: `GET`, `PUT` y `DELETE` son idempotentes en estado; `POST` valida duplicados y responde `409 Conflict` si el GTIN ya existe.
                                - **Sin autenticación**: Abierta para facilitar pruebas automatizadas locales.
                                """))
                .servers(List.of(new Server().url("http://localhost:8080").description("Servidor local")));
    }
}
