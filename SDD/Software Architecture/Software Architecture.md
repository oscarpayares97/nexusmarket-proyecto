# Software Architecture: NexusMarket

## Estilo
Arquitectura hexagonal (puertos y adaptadores). El dominio no depende de ningún framework.

## Capas
| Capa | Paquete | Responsabilidad |
|---|---|---|
| Dominio | application.domain | Modelos, objetos de valor, excepciones, puertos y servicios con las reglas de negocio |
| Adaptadores | application.adapters | Persistencia (JPA), REST (controladores y DTOs) y casos de uso por rol |
| Infraestructura | application.infrastructure | Seguridad, configuración y servicios técnicos |

## Reglas de dependencia
- El dominio no importa nada de adapters ni de infrastructure.
- Los adaptadores implementan los puertos de salida (domain.ports.out).
- Los controladores REST llaman a los puertos de entrada (domain.ports.in).

## Roles del sistema
Comprador, Vendedor, Operador Logístico, Administrador y Supervisor (un rol por usuario).