# Peso Bank — Session Handoff

**Date:** 2026-09-18
**Stack:** Java 21+ / Spring Boot / PostgreSQL / Lombok / Jakarta Validation
**Package root:** `com.ciicc.peso_bank`

## Where things stand

Layers that exist and are populated:

- `entity/` — `User`, `Account`, `UserProfile`, `Transaction`, `Audit`
- `dto/` — `UserDto`, `UserCreateRequest`, `UserUpdateRequest`, `UserProfileDto`, `UserProfileUpdate`
- `repository/` — `UserRepository`, `AccountRepository`, `TransactionRepository`, `AuditRepository`

Layers that do **not** exist yet:

- `service/` — no business logic layer at all
- `mapper/` — no entity↔DTO conversion layer
- `controller/` — folder exists but is empty, no endpoints exposed

Nothing is wired end-to-end yet. Repositories exist but nothing calls them.

## What got fixed this session

Two repository queries referenced fields that don't exist on the entities (would have thrown `PropertyReferenceException` / query parse errors at startup):

- `UserRepository.findWithProfileById` — `WHERE u.id = :id` → fixed to `WHERE u.userId = :id` (entity field is `userId`, not `id`)
- `AccountRepository` — `LEFT JOIN a.users` → fixed to `LEFT JOIN a.user` (the relation on `Account` is a singular `@OneToOne`, not a collection); `WHERE a.id = :id` → fixed to `WHERE a.accountId = :id`

Both were edited in place at their real paths in `repository/`.

## Design decisions made this session

**Mapper approach:** going manual (plain classes with static/`@Component` methods), not MapStruct. Reasoning: this is a learning project, manual mapping keeps the conversion logic visible instead of generated at compile time. Revisit MapStruct only if hand-written mappers start feeling repetitive across many entities.

**Layering discipline agreed on:**
`Controller` → `Service` (business logic, orchestrates repository + mapper, transaction boundaries) → `Repository` (persistence) → `Entity`, with `Mapper` sitting between `Service` and the DTOs going in/out.
Controller should stay thin — HTTP binding, `@Valid`, status codes — no business logic and no direct repository calls.

## Next steps (in order)

1. Build mappers: `UserMapper`, `AccountMapper` (and eventually `TransactionMapper`, `AuditMapper`) — entity → DTO for reads, DTO → entity for writes.
2. Build `UserService` — user creation (hash password, check `confirmPassword` match from `UserCreateRequest`), fetch-with-profile logic reusing the now-fixed repository queries.
3. Build `AccountService` — balance/debt logic, account creation tied to a `User`.
4. Build `UserController` / `AccountController` — thin REST layer on top of the services above.
5. Haven't yet looked closely at `Transaction` and `Audit` entities/repositories or their DTOs — worth a pass once User/Account flow works end-to-end, since deposit/withdraw/transfer will depend on them.

## Open questions / things to watch

- `Account.isActive` is a primitive `boolean` with Lombok `@Getter` — double check the generated getter is `isActive()` not `getActive()` if anything (e.g. a mapper) references it by name.
- No `@Valid` enforcement point exists yet since there's no controller — once built, confirm `UserCreateRequest`/`UserProfileDto` validation annotations actually fire.
- Password hashing isn't implemented anywhere yet (entity just stores a `password` column, length 60 suggests bcrypt was already planned).
