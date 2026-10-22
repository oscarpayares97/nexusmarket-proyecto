# Software Architecture: NexusMarket

## Estilo
Arquitectura hexagonal (puertos y adaptadores) con diseño guiado por el dominio (DDD). El dominio no depende de ningún framework: no importa Spring, JPA, HTTP ni JSON.

## Tecnologías
| Elemento | Tecnología |
|---|---|
| Lenguaje y framework | Java 17, Spring Boot 4.1.0, Maven |
| Persistencia | Spring Data JPA (Hibernate) sobre MySQL 8 |
| Seguridad | Spring Security, JWT sin estado (HS256), BCrypt |
| Despliegue | Docker Compose (MySQL + aplicación) |
| Pruebas | JUnit 5, Mockito, H2 en memoria, script de humo (46 comprobaciones) |

## Capas
| Capa | Paquete | Responsabilidad |
|---|---|---|
| Dominio | `application.domain` | Modelos, objetos de valor, excepciones, puertos y servicios con las reglas de negocio |
| Adaptadores de entrada | `application.adapters.rest` | Controladores REST, DTOs, mappers y manejo global de errores |
| Adaptadores de casos de uso | `application.adapters.useCases` | Una clase transaccional por rol que implementa el puerto de entrada y delega en los servicios del dominio |
| Adaptadores de salida | `application.adapters.persistence` | Adaptadores JPA que implementan los puertos de salida sobre MySQL |
| Adaptadores de seguridad | `application.adapters.security` | JWT, filtro de autenticación, `SecurityConfig`, BCrypt y CORS |
| Configuración | `application.adapters.config` | Registro de los servicios del dominio como beans (`DomainBeansConfig`) |
| Infraestructura | `application.infrastructure` | Carga inicial de datos (`DatabaseSeeder`) |

## Reglas de dependencia
- El dominio no importa nada de `adapters` ni de `infrastructure`.
- Los adaptadores de salida implementan los puertos de salida (`domain.ports.out`).
- Los controladores REST llaman únicamente a los puertos de entrada (`domain.ports.in`), uno por rol.
- Los servicios del dominio son clases simples sin anotaciones de Spring.
- Flujo de una petición: `REST -> puerto de entrada -> caso de uso -> servicio de dominio -> puerto de salida -> adaptador`.

## Dominio en cifras
| Elemento | Cantidad |
|---|---|
| Modelos | 17 |
| Objetos de valor / enumeraciones | 3 / 12 |
| Servicios de dominio | 54 (más 2 auxiliares) |
| Puertos de entrada | 6 (público, comprador, vendedor, operador logístico, administrador, supervisor) |
| Puertos de salida | 13 |
| Endpoints REST | 77 |
| Entidades JPA / tablas | 12 / 17 |

## Roles del sistema
Comprador, Vendedor, Operador Logístico, Administrador y Supervisor (un rol por usuario, regla RG-02), más el acceso público de visitante.

## Seguridad
- Autenticación sin estado con JWT; no hay sesión en el servidor.
- Autorización en dos niveles: por prefijo de ruta (`/api/v1/<rol>/**`) en `SecurityConfig` y por servicio de dominio (`requireRole` y propiedad del recurso).
- El filtro recarga al usuario desde la base de datos en cada petición, de modo que un usuario bloqueado pierde el acceso de inmediato.

## Pendiente
Adaptador de auditoría en MongoDB, a la espera de la decisión del profesor.

## Documentación relacionada
- Modelo C4 (4 niveles, capas y flujos): `SDD/C4/README.md`
- Dominio: `SDD/domain/`
- Adaptadores y endpoints: `SDD/adapters/`