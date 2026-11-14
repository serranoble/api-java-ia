# Products API — Práctica de Automatización QA

API REST desarrollada en **Java 21 + Spring Boot 3** con base de datos local **SQLite**, documentación **Swagger / OpenAPI 3** y **logs en formato JSON**, pensada para practicar automatización de casos de prueba sin necesidad de autenticación ni configuración de base de datos externa.

---

## Cómo levantar la API de manera local (Paso a paso)

El proyecto incluye **Maven Wrapper** (`mvnw.cmd` / `./mvnw`) y el driver embebido de **SQLite** (que crea automáticamente el archivo `products.db` en la raíz del proyecto), por lo que **solo necesitás tener Java 21 (JDK)** instalado.

---

### Paso a paso en Windows

1. **Instalar Java 21** (si todavía no lo tenés):
   Abrí **PowerShell** y ejecutá:
   ```powershell
   winget install --id EclipseAdoptium.Temurin.21.JDK --accept-package-agreements --accept-source-agreements
   ```
   *(Cerrá y volvé a abrir la terminal después de instalar para que reconozca el comando `java`.)*

2. **Verificar que Java 21 esté disponible:**
   ```powershell
   java -version
   ```
   Deberías ver una versión `21.x.x`.

3. **Ubicarte en la carpeta del proyecto:**
   ```powershell
   cd "ruta\al\proyecto\api_java"
   ```

4. **Levantar la API:**
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

5. **Verificar que esté funcionando:**
   Abrí tu navegador en http://localhost:8080/swagger-ui.html.
   *(Para detener la API, presioná `Ctrl + C` en la terminal.)*

6. *(Opcional)* **Correr las pruebas automatizadas:**
   ```powershell
   .\mvnw.cmd test
   ```

---

### Paso a paso en macOS

1. **Instalar Java 21** (usando [Homebrew](https://brew.sh/)):
   ```bash
   brew install --cask temurin@21
   ```

2. **Verificar que Java 21 esté disponible:**
   ```bash
   java -version
   ```

3. **Ubicarte en la carpeta del proyecto:**
   ```bash
   cd /ruta/al/proyecto/api_java
   ```

4. **Dar permiso de ejecución al Maven Wrapper** (solo la primera vez):
   ```bash
   chmod +x mvnw
   ```

5. **Levantar la API:**
   ```bash
   ./mvnw spring-boot:run
   ```

6. **Verificar que esté funcionando:**
   Abrí tu navegador en http://localhost:8080/swagger-ui.html.
   *(Para detener la API, presioná `Ctrl + C` en la terminal.)*

7. *(Opcional)* **Correr las pruebas automatizadas:**
   ```bash
   ./mvnw test
   ```

---

### Paso a paso en Linux (Ubuntu / Debian)

1. **Instalar Java 21:**
   ```bash
   sudo apt update
   sudo apt install -y openjdk-21-jdk
   ```
   *(En otras distros como Fedora/Arch podés instalar `java-21-openjdk-devel` o `jdk21-openjdk`.)*

2. **Verificar que Java 21 esté disponible:**
   ```bash
   java -version
   ```

3. **Ubicarte en la carpeta del proyecto:**
   ```bash
   cd /ruta/al/proyecto/api_java
   ```

4. **Dar permiso de ejecución al Maven Wrapper** (solo la primera vez):
   ```bash
   chmod +x mvnw
   ```

5. **Levantar la API:**
   ```bash
   ./mvnw spring-boot:run
   ```

6. **Verificar que esté funcionando:**
   Abrí tu navegador en http://localhost:8080/swagger-ui.html.
   *(Para detener la API, presioná `Ctrl + C` en la terminal.)*

7. *(Opcional)* **Correr las pruebas automatizadas:**
   ```bash
   ./mvnw test
   ```

---

## Capítulo adicional: Cómo ejecutarlo con Docker (autocontenido)

Si preferís no instalar Java en tu máquina, podés levantar la API dentro de un contenedor Docker.

### Requisitos
- Tener **Docker** (o **Docker Desktop** en Windows/macOS) instalado y en ejecución.

### Opción 1: Docker Compose (recomendado, persiste los datos de SQLite)
1. Ubicarse en la raíz del proyecto y ejecutar:
   ```bash
   docker compose up --build
   ```
2. Para detenerlo y conservar los datos: `Ctrl + C` o `docker compose down`.

### Opción 2: Docker CLI directo
1. Construir la imagen:
   ```bash
   docker build -t products-api:1.0.0 .
   ```
2. Levantar el contenedor:
   ```bash
   docker run -p 8080:8080 --name products-api products-api:1.0.0
   ```
   *(Si querés persistir la base SQLite entre reinicios, agregá `-v products-data:/app/data` al comando `docker run`.)*

Una vez iniciado el contenedor, la API responderá en `http://localhost:8080` y Swagger en `http://localhost:8080/swagger-ui.html`.

---

## Swagger / OpenAPI

Una vez iniciada la aplicación, podés explorar y probar todos los endpoints desde:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
- **OpenAPI YAML:** http://localhost:8080/v3/api-docs.yaml

---

## Modelo de datos (`products`)

| Campo         | Tipo           | Obligatorio | Editable | Descripción |
|---------------|----------------|-------------|----------|-------------|
| `gtin`        | `String`       | Sí          | **No**   | Clave primaria. Código numérico de 8 a 14 dígitos (`^\d{8,14}$`). No puede modificarse luego de crearse. |
| `name`        | `String`       | Sí          | Sí       | Nombre del producto (máximo 150 caracteres). |
| `description` | `String`       | No          | Sí       | Descripción del producto (máximo 1000 caracteres). |
| `price`       | `BigDecimal`   | Sí          | Sí       | Precio unitario (`>= 0.01`, hasta 10 enteros y 2 decimales). |

---

## Endpoints disponibles

Base path: `/api/v1/products`

| Método   | Ruta                       | Descripción                                      | Códigos de respuesta |
|----------|----------------------------|--------------------------------------------------|----------------------|
| `GET`    | `/api/v1/products/{gtin}`  | Obtiene todos los datos de un producto por GTIN. | `200`, `400`, `404`  |
| `GET`    | `/api/v1/products`         | Lista productos paginados (`page`, `size`, `sortBy`, `sortDir`). | `200`, `400` |
| `POST`   | `/api/v1/products`         | Crea un producto nuevo (devuelve header `Location`). | `201`, `400`, `409` |
| `PUT`    | `/api/v1/products/{gtin}`  | Actualiza `name`, `description` y `price` (idempotente; `gtin` inmutable). | `200`, `400`, `404` |
| `DELETE` | `/api/v1/products/{gtin}`  | Elimina un producto por GTIN.                    | `204`, `400`, `404`  |

### Parámetros de paginación (`GET /api/v1/products`)
- `page` (default `0`): número de página (base 0).
- `size` (default `10`): cantidad de registros por página (mínimo `1`, máximo `100`).
- `sortBy` (default `gtin`): campo de ordenamiento (`gtin`, `name`, `description`, `price`).
- `sortDir` (default `asc`): dirección (`asc` o `desc`).

### Formato estándar de errores
Ante cualquier error (`400`, `404`, `405`, `409`, `500`), la API responde un JSON uniforme:
```json
{
  "timestamp": "2026-11-13T21:20:00-03:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en los campos enviados",
  "path": "/api/v1/products",
  "fieldErrors": {
    "gtin": "El 'gtin' debe contener entre 8 y 14 dígitos numéricos"
  }
}
```

---

## Colección de Bruno

El repositorio incluye una colección lista para **Bruno** en la carpeta `bruno-collection/`:

1. Abrir **Bruno** → **Open Collection** → seleccionar la carpeta `bruno-collection/`.
2. Seleccionar el entorno **`local`** (arriba a la derecha), que apunta a `http://localhost:8080`.
3. Ejecutar los requests en orden (`01` al `08`) o correr toda la colección con **Run**; cada request ya incluye aserciones (`tests`) para validar códigos de estado y respuestas.

---

## Logs en formato JSON

La aplicación utiliza el formato estructurado `logstash` de Spring Boot junto con un filtro HTTP (`RequestLoggingFilter`) que registra cada petición (`httpMethod`, `path`, `status`, `durationMs`) y cada operación de servicio en formato JSON línea por línea en la consola.
