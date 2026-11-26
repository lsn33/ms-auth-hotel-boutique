# ms-auth-hotel-boutique

Microservicio de usuarios (`usuarios-service`, puerto 8081) de AndesStay / Hotel Boutique (DSY1107, EP2 y EP5).
Sincroniza usuarios de Cognito, los persiste en PostgreSQL (Neon) y participa en la mensajería asíncrona con RabbitMQ y Apache Kafka. Kafka convive con RabbitMQ: no lo reemplaza y no altera sus colas.

## Requisitos
- Java 21, Maven 3.9+
- RabbitMQ (clúster de 2 nodos, ver repo `ms-rabbit-admin`, carpeta `infra/`)
- Apache Kafka (3 Zookeeper + 3 brokers + Kafka UI, ver repo `ms-kafka-admin`, carpeta `infra/`)
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

## Streaming de eventos (Apache Kafka)
Paquete `com.hotelboutique.auth.kafka`. Los tópicos se crean con `ms-kafka-admin` (`POST /topics/inicializar`).

| Tópico | Config | Clave del mensaje | Rol de este servicio |
|---|---|---|---|
| `hotel.usuarios.eventos` | 3 particiones, factor de replicación 3, `min.insync.replicas=2`, `cleanup.policy=compact` (conserva el último evento por usuario) | `email` | Productor |
| `hotel.reservas.eventos` | 3 particiones, factor de replicación 3, `min.insync.replicas=2`, `cleanup.policy=delete`, retención 7 días | `habitacionId` | Consumidor |

### Productor
`UsuarioController` (`POST /usuarios/sync`) llama a `UsuarioKafkaPublisher`, que publica un `UsuarioStreamEvent` con `KafkaTemplate`. La clave es el email: todos los eventos de un usuario van a la misma partición y el tópico compactado conserva el último.
Un fallo de Kafka solo se registra en logs: el login y la sincronización no se ven afectados. `KAFKA_ENABLED=false` apaga Kafka por completo.

### Eventos (DTO, JSON)
| DTO | Campos | Sentido |
|---|---|---|
| `UsuarioStreamEvent` | `eventId`, `tipo` (`usuario.sincronizado`), `email`, `nombre`, `ocurridoEn` | Lo produce este servicio |
| `ReservaStreamEvent` | `eventId`, `tipo`, `reservaId`, `usuarioEmail`, `habitacionId`, `fechaCheckin`, `fechaCheckout`, `estado`, `ocurridoEn` | Lo produce ms-reservas; aquí es la copia del lado consumidor (solo importa quién hizo qué) |

### Consumidor y consumer group
| Consumer group | Clase (`@KafkaListener`) | Tópico | Qué hace |
|---|---|---|---|
| `auth-actividad-usuario` | `ActividadUsuarioConsumer` | `hotel.reservas.eventos` | Calcula la actividad de cada usuario (total de reservas, canceladas, último evento) en `ActividadUsuarioService`, separada de la mensajería |

Este grupo lee el mismo tópico que `reservas-ocupacion` (ms-reservas), pero cada grupo recibe todos los eventos de forma independiente. Con RabbitMQ habría que duplicar la cola.
La actividad se consulta en `GET /usuarios/mi-actividad` (usuario autenticado).

### Manejo de errores
- `ErrorHandlingDeserializer`: un JSON roto no bloquea la partición; no se reintenta, se descarta y se registra.
- `DefaultErrorHandler` (`KafkaErrorHandlingConfig`): 3 reintentos con espera creciente (1 s, 2 s, 4 s). Si se agotan, se registra en logs el tópico, la partición, el offset y la causa, y se sigue con el siguiente mensaje.
- Payload inválido (sin `tipo`, `reservaId` o `usuarioEmail`): se registra una advertencia y se avanza.
- Productor: `acks=all`, idempotencia activada y `max.block.ms=3000` para no bloquear la petición HTTP si Kafka está caído.
- Consumidor: `enable-auto-commit=false` y `ack-mode=record` (el offset se confirma después de procesar cada registro), `auto-offset-reset=earliest`.

## Variables de entorno (sin valores)
Se leen desde un archivo `.env` (no versionado). Copiar `.env.example` a `.env`.

| Variable | Uso |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` (H2 en memoria) o `prod` (PostgreSQL) |
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | Conexión a PostgreSQL (perfil `prod`) |
| `AWS_REGION`, `COGNITO_USER_POOL_ID` | Validación del JWT (issuer de Cognito) |
| `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_VHOST` | Broker (IP privada de la EC2 del clúster) |
| `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD` | Credenciales del broker |
| `KAFKA_BOOTSTRAP_SERVERS` | Brokers Kafka (IP privada de la EC2 de Kafka, puertos 9092, 9093 y 9094; en local `localhost`) |
| `KAFKA_ENABLED` | `true` (por defecto) activa productor y consumidor Kafka; `false` los apaga |
| `DLQ_CONSUMIR` | `true` (por defecto) activa el listener de la DLQ; `false` lo apaga |

## Ejecutar y probar
```bash
mvn clean test
mvn spring-boot:run
```
