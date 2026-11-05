# ms-auth-hotel-boutique

Microservicio de usuarios (`usuarios-service`, puerto 8081) de AndesStay / Hotel Boutique (DSY1107, EP2).
Sincroniza usuarios de Cognito, los persiste en PostgreSQL (Neon) y participa en la mensajería asíncrona con RabbitMQ.

## Requisitos
- Java 21, Maven 3.9+
- RabbitMQ (clúster de 2 nodos, ver repo `ms-rabbit-admin`, carpeta `infra/`)
- Pool de Amazon Cognito (IDaaS) y base PostgreSQL

## Mensajería (RabbitMQ)
| Rol | Detalle |
|---|---|
| Productor | `UsuarioEventPublisher` publica `usuario.registrado` en el exchange topic `hotel.eventos` |
| Consumidor | `NotificacionConsumer` escucha `q.notificaciones` (routing keys `usuario.registrado` y `reserva.#`) |
| DLQ | `DlqConsumer` escucha `q.notificaciones.dlq` y registra en logs los mensajes no entregados |
| Confirmación | ACK manual. Mensaje inválido o JSON roto: NACK sin requeue y pasa a la DLQ |
| Reintentos | Errores recuperables: 3 intentos con backoff (1 s, x2, máx. 5 s); si persiste, DLQ |
| Retención | Colas principales: TTL 24 h y máx. 10.000 mensajes (configuración en `RabbitTopologyConfig`) |

## Variables de entorno (sin valores)
Se leen desde un archivo `.env` (no versionado). Copiar `.env.example` a `.env`.

| Variable | Uso |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` (H2 en memoria) o `prod` (PostgreSQL) |
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | Conexión a PostgreSQL (perfil `prod`) |
| `AWS_REGION`, `COGNITO_USER_POOL_ID` | Validación del JWT (issuer de Cognito) |
| `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_VHOST` | Broker (IP privada de la EC2 del clúster) |
| `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD` | Credenciales del broker |
| `DLQ_CONSUMIR` | `true` (por defecto) activa el listener de la DLQ; `false` lo apaga |

## Ejecutar y probar
```bash
mvn clean test
mvn spring-boot:run
```
