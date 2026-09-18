# Project Concepts Cheat Sheet

How the layers in this Spring Boot app connect, using `Account` as the example flow.

## The flow (request → response)

```
Client → Controller → Service → Repository → Database
                ↕
              Mapper (Entity ↔ DTO)
```

1. **Controller** receives the HTTP request.
2. **Controller** calls a **Service** method.
3. **Service** talks to the **Repository** to read/write data, applies business rules.
4. **Service** uses a **Mapper** to convert **Entity** ↔ **DTO**.
5. **Controller** sends the **DTO** back as the response.

The database (Entity) never goes directly to the client — the DTO is the "safe" version we expose.

## Entity — `entity/Account.java`

A Java class that maps 1:1 to a database table (`@Entity`, `@Table`, `@Column`). Holds raw data exactly as stored (e.g. `Account.isActive`, `Account.balance`). Used only inside Service/Repository — never returned directly to the client.

## Repository — `repository/AccountRepository.java`

The layer that talks to the database. Just an interface extending `JpaRepository<Account, Long>` — Spring auto-generates the SQL. You only write custom queries when needed (`@Query`, or method names like `existsByAccountNumber`).

## Service — `service/AccountService.java`

Where the business logic lives: validation, rules like "user can only have one account", generating account numbers, wrapping DB calls in `@Transactional`. It's the middleman between Controller and Repository, and it's where Entity gets converted to DTO before returning.

## DTO (Data Transfer Object) — `dto/AccountDto.java`, `AccountCreateRequest.java`

Plain `record`s that define exactly what data goes **in** (request DTOs, e.g. `AccountCreateRequest`) and **out** (response DTOs, e.g. `AccountDto`) of the API. Keeps internal entity fields (and relationships) from leaking out, and lets you shape the API independently from the DB schema.

## Mapper — `mapper/AccountMapper.java`

Converts between Entity and DTO automatically, using MapStruct (`@Mapper(componentModel = "spring")`). You just declare the interface + field mappings; MapStruct generates the implementation at build time. Avoids writing repetitive `dto.setX(entity.getX())` code by hand.

## Controller — `controller/AccountController.java`

The entry point for HTTP requests (`@RestController`, `@RequestMapping`). Defines the endpoints (`@GetMapping`, `@PostMapping`), takes in request DTOs, calls the Service, and returns response DTOs. Has no business logic — just routes the request.

## Config — `config/PasswordConfig.java`

Setup code that isn't tied to one feature — beans, security settings, encoders, etc. (`@Configuration` + `@Bean`). Example: `PasswordConfig` defines the `PasswordEncoder` bean used app-wide for hashing passwords.

## Quick summary table

| Layer | Talks to | Job |
|---|---|---|
| Controller | Service | Handle HTTP, route requests/responses |
| Service | Repository, Mapper | Business logic, validation, transactions |
| Repository | Database | CRUD / queries |
| Entity | Database table | Raw data shape |
| DTO | Controller/Client | API-facing data shape |
| Mapper | Entity ↔ DTO | Convert between the two |
| Config | Spring context | App-wide setup (beans, security, etc.) |
