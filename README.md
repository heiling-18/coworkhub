# CoWorkHub API

API REST (Spring Boot 4.1, Java 21, MySQL 8.0) para gestionar reservas de una red de coworking.

## 1. Base de datos

### Opcion B: Docker (recomendada)
```bash
docker compose up -d
docker compose logs -f mysql     # esperar "ready for connections" y salir con Ctrl+C
```
Sin docker compose (una sola linea en Windows):
```bash
docker run -d --name coworkhub-mysql -e MYSQL_DATABASE=coworkhub -e MYSQL_USER=coworkhub -e MYSQL_PASSWORD=coworkhub -e MYSQL_ROOT_PASSWORD=root -p 3307:3306 -v coworkhub-datos:/var/lib/mysql mysql:8.0
```
Comandos utiles:
| Accion | Comando |
|---|---|
| Ver contenedores | `docker ps` |
| Detener / iniciar | `docker compose stop` / `docker compose start` |
| Consola MySQL | `docker exec -it coworkhub-mysql mysql -u coworkhub -p coworkhub` |
| Borrar todo (datos incluidos) | `docker compose down -v` |

### Opcion A: MySQL instalado
Crear la base `coworkhub` y el usuario `coworkhub` / `coworkhub` con permisos sobre ella.

## 2. Ejecutar
```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

## 3. URLs
- Salud: http://localhost:8080/actuator/health
- Swagger UI: http://localhost:8080/doc/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs (exportado en `docs/openapi.json`)

## 4. Usuarios de prueba
_(se completa en la fase de seguridad)_

## 5. Estructura
```
src/main/java/com/coworkhub
├── config/                    @Configuration, @Bean (Clock, PasswordEncoder)
├── persistence/
│   ├── entity/                entidades JPA
│   ├── enums/                 RoomType, MemberStatus, BookingStatus, Role
│   └── repository/            Spring Data JPA
├── business/
│   ├── service/               interfaces
│   ├── service/impl/          implementaciones + reglas RN-01..RN-13
│   ├── mapper/                entidad <-> DTO
│   └── exception/             excepciones propias
├── presentation/
│   ├── controller/            @RestController
│   ├── dto/request|response/  records
│   └── advice/                @RestControllerAdvice (formato unico de error)
└── security/                  JWT (fase 08)
docs/        openapi.json
postman/     coleccion con los 14 escenarios
```
