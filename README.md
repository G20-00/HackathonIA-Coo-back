# Coomeva Hackathon - Backend API

## 🚀 Descripción

API REST desarrollada con Spring Boot 3.5 y Java 21 para la gestión integral de servicios de Coomeva en Cali. 
Incluye autenticación JWT, gestión de servicios, órdenes, pagos y alianzas empresariales.

## 📋 Tecnologías

- **Java 21**
- **Spring Boot 3.5.9**
- **Spring Security** con JWT
- **Spring Data JPA**
- **MySQL 8.0**
- **Maven**
- **Lombok**
- **Swagger/OpenAPI 3**
- **Docker** (opcional)

## 🏗️ Arquitectura

```
src/main/java/com/coomeva/hackathon/
├── config/              # Configuración (Security, DataInitializer)
├── controller/          # REST Controllers
├── dto/                 # Data Transfer Objects
├── entity/              # Entidades JPA
├── repository/          # Repositorios JPA
├── security/            # JWT y seguridad
├── service/             # Lógica de negocio
└── exception/           # Manejo de excepciones
```

## ⚙️ Configuración

### 1. Requisitos Previos

- JDK 21
- MySQL 8.0
- Maven 3.8+

### 2. Base de Datos

#### Opción A: Usar Docker (Recomendado)

Ejecuta MySQL en un contenedor Docker:

```bash
# Levantar MySQL
docker compose up -d

# Verificar que esté corriendo
docker ps

# Ver logs
docker compose logs -f mysql

# Detener MySQL
docker compose down

# Detener y eliminar datos
docker compose down -v
```

El contenedor MySQL se configura automáticamente con:
- Usuario: `hackathon`
- Contraseña: `hackathon123`
- Base de datos: `coomeva_hackathon`
- Puerto: `3306`

#### Opción B: Instalación Local de MySQL

Crea la base de datos MySQL:

```sql
CREATE DATABASE coomeva_hackathon CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

O configura la propiedad `createDatabaseIfNotExist=true` en el archivo `application.properties` (ya configurada).

### 3. Configuración de application.properties

Actualiza las credenciales de MySQL en `src/main/resources/application.properties`:

**Si usas Docker (Opción A):**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/coomeva_hackathon?createDatabaseIfNotExist=true
spring.datasource.username=hackathon
spring.datasource.password=hackathon123
```

**Si usas MySQL local (Opción B):**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/coomeva_hackathon?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=tu_contraseña
```

### 4. Instalación

```bash
# Clonar el repositorio
cd HackathonIA-Coo-back

# Compilar el proyecto
./mvnw clean install

# O en Windows
mvnw.cmd clean install
```

### 5. Ejecutar la Aplicación

```bash
./mvnw spring-boot:run

# O en Windows
mvnw.cmd spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8081`

## 📚 Documentación API

### Swagger UI

Accede a la documentación interactiva de la API:

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI Docs**: http://localhost:8081/api-docs

## 🔐 Autenticación

La API usa JWT (JSON Web Tokens) para autenticación.

### Usuarios de Prueba

Al iniciar la aplicación, se crean automáticamente:

**Administrador:**
- Email: `admin@coomeva.com`
- Password: `admin123`

**Usuario Regular:**
- Email: `usuario@coomeva.com`
- Password: `usuario123`

### Flujo de Autenticación

1. **Registro de Usuario**
   ```http
   POST /api/auth/register
   Content-Type: application/json

   {
     "email": "nuevo@ejemplo.com",
     "password": "password123",
     "firstName": "Juan",
     "lastName": "Pérez",
     "documentNumber": "1234567890",
     "phone": "3001234567"
   }
   ```

2. **Login**
   ```http
   POST /api/auth/login
   Content-Type: application/json

   {
     "email": "usuario@coomeva.com",
     "password": "usuario123"
   }
   ```

   **Respuesta:**
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     "email": "usuario@coomeva.com",
     "firstName": "Juan",
     "lastName": "Pérez",
     "role": "USER"
   }
   ```

3. **Usar el Token**
   
   Incluye el token en las peticiones protegidas:
   ```http
   Authorization: Bearer {token}
   ```

## 🛠️ Endpoints Principales

### Autenticación
- `POST /api/auth/register` - Registro de usuario
- `POST /api/auth/login` - Login

### Servicios
- `GET /api/services` - Listar todos los servicios
- `GET /api/services/available` - Servicios disponibles
- `GET /api/services/{id}` - Obtener servicio por ID
- `GET /api/services/type/{type}` - Filtrar por tipo (SALUD, EDUCACION, SEGUROS, CREDITOS, RECREACION)
- `GET /api/services/search?keyword={keyword}` - Buscar servicios
- `POST /api/services` - Crear servicio (Admin)
- `PUT /api/services/{id}` - Actualizar servicio (Admin)
- `DELETE /api/services/{id}` - Eliminar servicio (Admin)

### Categorías
- `GET /api/categories` - Listar categorías
- `GET /api/categories/{id}` - Obtener categoría por ID
- `POST /api/categories` - Crear categoría (Admin)
- `PUT /api/categories/{id}` - Actualizar categoría (Admin)
- `DELETE /api/categories/{id}` - Eliminar categoría (Admin)

### Alianzas
- `GET /api/alliances` - Listar alianzas
- `GET /api/alliances/active` - Alianzas activas
- `GET /api/alliances/{id}` - Obtener alianza por ID
- `GET /api/alliances/search?keyword={keyword}` - Buscar alianzas
- `POST /api/alliances` - Crear alianza (Admin)
- `PUT /api/alliances/{id}` - Actualizar alianza (Admin)
- `DELETE /api/alliances/{id}` - Eliminar alianza (Admin)

### Órdenes
- `GET /api/orders/my-orders` - Mis órdenes (Usuario)
- `GET /api/orders/{id}` - Obtener orden por ID
- `POST /api/orders` - Crear orden
  ```json
  {
    "items": [
      {
        "serviceId": 1,
        "quantity": 1
      }
    ],
    "notes": "Orden de prueba"
  }
  ```
- `PATCH /api/orders/{id}/status?status={status}` - Actualizar estado (Admin)

### Pagos
- `POST /api/payments/process` - Procesar pago (Modo Sandbox)
  ```json
  {
    "orderId": 1,
    "paymentMethod": "CREDIT_CARD",
    "cardNumber": "4111111111111111",
    "cardHolderName": "Juan Pérez",
    "expiryDate": "12/26",
    "cvv": "123"
  }
  ```
- `GET /api/payments/order/{orderId}` - Obtener pago por orden
- `GET /api/payments/{id}` - Obtener pago por ID

## 📊 Tipos de Servicios

La aplicación maneja 5 categorías principales:

1. **SALUD** - Planes médicos, asistencias, prevención
2. **EDUCACION** - Créditos educativos, cursos, becas
3. **SEGUROS** - Vida, salud, vehículos, hogar
4. **CREDITOS** - Préstamos personales, vivienda, ahorro
5. **RECREACION** - Turismo, clubes, actividades

## 🔄 Datos Iniciales

Al iniciar la aplicación por primera vez, se cargan automáticamente:

- ✅ 5 Categorías
- ✅ 20 Servicios (4 por categoría)
- ✅ 5 Alianzas empresariales
- ✅ 2 Usuarios (Admin y Usuario regular)

## 🧪 Pruebas con Postman/Insomnia

### 1. Registrar un nuevo usuario
```http
POST http://localhost:8081/api/auth/register
Content-Type: application/json

{
  "email": "test@ejemplo.com",
  "password": "test123",
  "firstName": "Test",
  "lastName": "Usuario",
  "documentNumber": "9999999999",
  "phone": "3009999999"
}
```

### 2. Login
```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "email": "test@ejemplo.com",
  "password": "test123"
}
```

### 3. Obtener servicios (no requiere auth)
```http
GET http://localhost:8081/api/services/available
```

### 4. Crear una orden (requiere auth)
```http
POST http://localhost:8081/api/orders
Authorization: Bearer {tu_token_aqui}
Content-Type: application/json

{
  "items": [
    {
      "serviceId": 1,
      "quantity": 1
    }
  ],
  "notes": "Primera orden"
}
```

### 5. Procesar pago
```http
POST http://localhost:8081/api/payments/process
Authorization: Bearer {tu_token_aqui}
Content-Type: application/json

{
  "orderId": 1,
  "paymentMethod": "CREDIT_CARD",
  "cardNumber": "4111111111111111",
  "cardHolderName": "Test Usuario",
  "expiryDate": "12/26",
  "cvv": "123"
}
```

## 🐳 Docker (Opcional)

### Docker Compose para MySQL

Crea un archivo `docker-compose.yml`:

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: coomeva-mysql
    environment:
      MYSQL_DATABASE: coomeva_hackathon
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

volumes:
  mysql-data:
```

Ejecutar:
```bash
docker-compose up -d
```

## 🔧 Configuración de CORS

El backend está configurado para aceptar peticiones desde:
- `http://localhost:3000` (Next.js development)
- `http://localhost:3001`

Para agregar más orígenes, edita `SecurityConfig.java`:

```java
configuration.setAllowedOrigins(List.of(
    "http://localhost:3000",
    "http://localhost:3001",
    "https://tu-dominio.com"
));
```

## 📈 Monitoreo

Spring Boot Actuator está habilitado:

- Health Check: http://localhost:8081/actuator/health
- Info: http://localhost:8081/actuator/info

## 🚨 Manejo de Errores

La API retorna errores en formato JSON:

```json
{
  "status": 400,
  "message": "Email already registered",
  "timestamp": "2026-01-23T10:30:00"
}
```

Códigos de estado comunes:
- `200` - OK
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `500` - Internal Server Error

## 🔐 Seguridad

- Passwords encriptados con BCrypt
- JWT con expiración de 24 horas
- CORS configurado
- CSRF deshabilitado (API REST stateless)
- Endpoints públicos: `/api/auth/**`, `/api/services/**`, `/api/categories/**`, `/api/alliances/**`
- Endpoints protegidos: Requieren token JWT

## 📝 Próximos Pasos

### Para el Frontend (Next.js):

1. Instalar axios o fetch para las peticiones HTTP
2. Implementar context para el estado de autenticación
3. Crear formularios de registro/login
4. Mostrar catálogo de servicios
5. Implementar carrito de compras
6. Integrar pasarela de pagos

### Ejemplo de integración en Next.js:

```typescript
// services/api.ts
import axios from 'axios';

const API_URL = 'http://localhost:8081/api';

export const api = axios.create({
  baseURL: API_URL,
});

// Interceptor para agregar token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Funciones de autenticación
export const auth = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
};

// Funciones de servicios
export const services = {
  getAll: () => api.get('/services/available'),
  getById: (id) => api.get(`/services/${id}`),
  getByType: (type) => api.get(`/services/type/${type}`),
};

// Funciones de órdenes
export const orders = {
  create: (data) => api.post('/orders', data),
  getMy: () => api.get('/orders/my-orders'),
};

// Funciones de pagos
export const payments = {
  process: (data) => api.post('/payments/process', data),
};
```

## 🤝 Contribución

Para la hackathon:

1. Crea una rama desde `jv_develop`
2. Realiza tus cambios
3. Haz commit con mensajes descriptivos
4. Crea un pull request

## 📞 Contacto y Soporte

Para dudas o problemas durante la hackathon, contacta al equipo de desarrollo.

## 📄 Licencia

Este proyecto es para fines de la hackathon Coomeva 2026.

---

**¡Buena suerte en la hackathon! 🚀**
