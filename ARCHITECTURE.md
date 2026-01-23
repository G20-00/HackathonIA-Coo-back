# Diagrama de Arquitectura - Backend Coomeva Hackathon

## Modelo de Datos (Entidades)

```
┌─────────────────────────────────────────────────────────────────────┐
│                        MODELO DE DATOS                               │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│    User      │       │   Category   │       │   Alliance   │
├──────────────┤       ├──────────────┤       ├──────────────┤
│ id           │       │ id           │       │ id           │
│ email*       │       │ name*        │       │ name         │
│ password     │       │ description  │       │ description  │
│ firstName    │       │ icon         │       │ logoUrl      │
│ lastName     │       └──────┬───────┘       │ contactEmail │
│ documentNumber│              │               │ contactPhone │
│ phone        │              │               │ website      │
│ address      │              │1              │ active       │
│ role         │              │               └───────┬──────┘
│ enabled      │              │                       │
└──────┬───────┘              │                       │
       │1                     │                       │
       │                      │                       │
       │                      │*                      │*
       │                 ┌────┴──────┐               │
       │                 │  Service  │◄──────────────┤
       │                 ├───────────┤               │
       │                 │ id        │    Many-to-   │
       │                 │ name      │     Many      │
       │                 │ description│              │
       │                 │ price     │               │
       │                 │ imageUrl  │               │
       │                 │ available │               │
       │                 │ type      │               │
       │                 └─────┬─────┘               │
       │                       │*                    │
       │                       │                     │
       │*                      │                     │
  ┌────┴──────┐               │                     │
  │   Order   │               │                     │
  ├───────────┤               │                     │
  │ id        │               │                     │
  │ orderNumber*│             │                     │
  │ totalAmount│              │                     │
  │ status    │               │                     │
  │ notes     │               │*                    │
  │ createdAt │          ┌────┴─────────┐          │
  └─────┬─────┘          │  OrderItem   │          │
        │1               ├──────────────┤          │
        │                │ id           │          │
        │                │ quantity     │          │
        │                │ price        │          │
        │1               │ subtotal     │          │
   ┌────┴──────┐         └──────────────┘          │
   │  Payment  │                                    │
   ├───────────┤                                    │
   │ id        │                                    │
   │ amount    │                                    │
   │ method    │                                    │
   │ status    │                                    │
   │ transactionId│                                 │
   │ completedAt│                                   │
   └───────────┘                                    │
```

## Tipos de Datos

### Service.ServiceType (Enum)
- SALUD
- EDUCACION
- SEGUROS
- CREDITOS
- RECREACION

### User.UserRole (Enum)
- USER
- ADMIN

### Order.OrderStatus (Enum)
- PENDING
- PROCESSING
- COMPLETED
- CANCELLED

### Payment.PaymentMethod (Enum)
- CREDIT_CARD
- DEBIT_CARD
- PSE
- CASH

### Payment.PaymentStatus (Enum)
- PENDING
- PROCESSING
- COMPLETED
- FAILED
- REFUNDED

## Flujo de la Aplicación

```
┌──────────────────────────────────────────────────────────────────────┐
│                     FLUJO DE AUTENTICACIÓN                            │
└──────────────────────────────────────────────────────────────────────┘

Usuario → POST /api/auth/register → AuthController
                                           ↓
                                      AuthService
                                           ↓
                                    UserRepository
                                           ↓
                                    JwtTokenProvider
                                           ↓
                                    JWT Token ← Usuario


Usuario → POST /api/auth/login → AuthController
                                       ↓
                                  AuthService
                                       ↓
                               AuthenticationManager
                                       ↓
                            CustomUserDetailsService
                                       ↓
                                JwtTokenProvider
                                       ↓
                                JWT Token ← Usuario
```

```
┌──────────────────────────────────────────────────────────────────────┐
│                     FLUJO DE COMPRA                                   │
└──────────────────────────────────────────────────────────────────────┘

1. Usuario navega servicios
   GET /api/services → ServiceController → ServiceService

2. Usuario crea orden
   POST /api/orders → OrderController → OrderService
                                              ↓
                                       [Calcula total]
                                              ↓
                                       OrderRepository

3. Usuario procesa pago
   POST /api/payments/process → PaymentController → PaymentService
                                                          ↓
                                                   [Simula Gateway]
                                                          ↓
                                                   PaymentRepository
                                                          ↓
                                                [Actualiza Order.status]
```

## Arquitectura de Seguridad

```
┌──────────────────────────────────────────────────────────────────────┐
│                     SEGURIDAD JWT                                     │
└──────────────────────────────────────────────────────────────────────┘

Request → JwtAuthenticationFilter
              ↓
         [Extrae Token]
              ↓
         JwtTokenProvider
              ↓
         [Valida Token]
              ↓
    CustomUserDetailsService
              ↓
        UserRepository
              ↓
    SecurityContext.setAuthentication()
              ↓
         Controller → @PreAuthorize("hasRole('...')")
```

## Endpoints y Permisos

```
┌──────────────────────────────────────────────────────────────────────┐
│                    ENDPOINTS Y ACCESO                                 │
└──────────────────────────────────────────────────────────────────────┘

PUBLIC (Sin autenticación):
├── POST   /api/auth/register
├── POST   /api/auth/login
├── GET    /api/services/**
├── GET    /api/categories/**
├── GET    /api/alliances/**
└── GET    /swagger-ui.html

AUTHENTICATED (USER o ADMIN):
├── GET    /api/orders/my-orders
├── GET    /api/orders/{id}
├── POST   /api/orders
├── POST   /api/payments/process
├── GET    /api/payments/order/{orderId}
└── GET    /api/payments/{id}

ADMIN ONLY:
├── POST   /api/services
├── PUT    /api/services/{id}
├── DELETE /api/services/{id}
├── POST   /api/categories
├── PUT    /api/categories/{id}
├── DELETE /api/categories/{id}
├── POST   /api/alliances
├── PUT    /api/alliances/{id}
├── DELETE /api/alliances/{id}
├── GET    /api/orders (todos)
└── PATCH  /api/orders/{id}/status
```

## Stack Tecnológico

```
┌──────────────────────────────────────────────────────────────────────┐
│                         TECNOLOGÍAS                                   │
└──────────────────────────────────────────────────────────────────────┘

Layer                    Technology
─────────────────────────────────────────────────────────────────────
Presentation Layer       REST Controllers, Swagger/OpenAPI
Security Layer           Spring Security + JWT
Business Logic Layer     Service Classes
Data Access Layer        Spring Data JPA
Database                 MySQL 8.0
Build Tool               Maven
Documentation            Swagger UI
Monitoring               Spring Boot Actuator
```

## Estructura de Paquetes

```
com.coomeva.hackathon
│
├── config/
│   ├── SecurityConfig.java          # Configuración de seguridad
│   └── DataInitializer.java         # Carga de datos iniciales
│
├── controller/
│   ├── AuthController.java          # Autenticación
│   ├── ServiceController.java       # CRUD Servicios
│   ├── CategoryController.java      # CRUD Categorías
│   ├── AllianceController.java      # CRUD Alianzas
│   ├── OrderController.java         # CRUD Órdenes
│   └── PaymentController.java       # Procesamiento pagos
│
├── dto/
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   ├── AuthResponse.java
│   ├── CreateOrderRequest.java
│   ├── OrderItemRequest.java
│   └── PaymentRequest.java
│
├── entity/
│   ├── User.java
│   ├── Category.java
│   ├── Service.java
│   ├── Alliance.java
│   ├── Order.java
│   ├── OrderItem.java
│   └── Payment.java
│
├── repository/
│   ├── UserRepository.java
│   ├── CategoryRepository.java
│   ├── ServiceRepository.java
│   ├── AllianceRepository.java
│   ├── OrderRepository.java
│   ├── OrderItemRepository.java
│   └── PaymentRepository.java
│
├── service/
│   ├── AuthService.java
│   ├── ServiceService.java
│   ├── CategoryService.java
│   ├── AllianceService.java
│   ├── OrderService.java
│   └── PaymentService.java
│
├── security/
│   ├── JwtTokenProvider.java           # Generación y validación JWT
│   ├── JwtAuthenticationFilter.java    # Filtro de autenticación
│   └── CustomUserDetailsService.java   # Carga de usuarios
│
├── exception/
│   └── GlobalExceptionHandler.java     # Manejo global de errores
│
└── HackathonApplication.java           # Clase principal
```

## Próximos Pasos de Integración

### Para conectar con Next.js Frontend:

1. **Configurar axios/fetch en Next.js**
2. **Crear Context de Autenticación**
3. **Implementar páginas:**
   - Login/Register
   - Catálogo de servicios
   - Detalle de servicio
   - Carrito de compras
   - Checkout/Pago
   - Mis órdenes
   - Panel admin (opcional)

4. **Variables de entorno (.env.local):**
   ```
   NEXT_PUBLIC_API_URL=http://localhost:8080/api
   ```

5. **Ejemplo de servicio API en Next.js:**
   ```typescript
   // lib/api.ts
   const API_URL = process.env.NEXT_PUBLIC_API_URL;
   
   export async function login(email, password) {
     const response = await fetch(`${API_URL}/auth/login`, {
       method: 'POST',
       headers: { 'Content-Type': 'application/json' },
       body: JSON.stringify({ email, password })
     });
     return response.json();
   }
   ```
```
