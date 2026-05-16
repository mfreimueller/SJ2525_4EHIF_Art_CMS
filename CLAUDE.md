# CLAUDE.md - Art CMS Project Guide

## Project Overview

This is a **Spring Boot 4.0** REST API for managing art exhibitions (teaching project for HTL 4th grade). It implements a **layered architecture combining DDD (Domain-Driven Design) with Spring Boot**, using a strict package-per-layer structure.

**Tech stack:** Spring Boot 4.0, Java 25, JPA/Hibernate, PostgreSQL, MapStruct, Lombok, Spring HATEOAS, Flyway, Spring REST Docs, Testcontainers.

## Package Structure (8 packages)

```
com.mfreimueller.art
├── commands/          # Input objects (command pattern)
├── domain/            # Domain entities, value objects, rich types
├── dto/               # Response/output transfer objects
├── foundation/        # Shared kernel: exceptions, factory beans
├── mappers/           # MapStruct interfaces (domain <-> DTO)
├── persistence/       # Spring Data repositories + JPA converters
├── presentation/      # REST controllers + HATEOAS model assemblers
├── richtypes/         # Rich types (value objects with validation)
├── service/           # Business logic (one per domain aggregate)
└── util/              # Shared utilities
```

## Layer Dependencies

```
presentation -> service -> persistence -> domain
     |              |
     v              v
   dto  <-------- mappers
commands (input) ---------------> service
richtypes ----------------------> domain
foundation ----------------------> service/domain
```

Each layer only depends on layers below it. No cross-layer violations (e.g., presentation never touches persistence directly).

## Entity Patterns

### AbstractEntity<T>
- Base class for all entities: `@MappedSuperclass`, `@SuperBuilder`, version field (`@Version` for optimistic locking)
- Provides `getId()` abstract method and `equals` based on ID

### HistoryBase<T>
- Extends `AbstractEntity<T>`, `@MappedSuperclass` (not `@Entity`)
- Adds audit fields: `createdAt`, `updatedAt`, `createdBy` (Creator), `updatedBy` (Creator)
- All persistent entities either extend `HistoryBase` or `AbstractEntity`

### Identifier Pattern
Two styles used depending on the entity:
1. **EmbeddedId (record):** `public record PointOfInterestId(@GeneratedValue Long id) {}` — for composite-key-like entities
2. **Standard Long @Id:** `private Long id` — for simple entities like `Content`

### Inheritance
Uses `InheritanceType.JOINED` for the `Content` hierarchy:
```
Content (abstract, @MappedSuperclass = ContentBase table)
├── TextContent
├── ImageContent
├── AudioContent
├── VideoContent
└── SlideshowContent
```

## Domain Entity Rules

- Always use `@NoArgsConstructor(access = AccessLevel.PROTECTED)` + `@AllArgsConstructor`
- Use `@SuperBuilder` (not `@Builder`) for entities extending base classes
- Use `@Getter`, `@Setter`, `@EqualsAndHashCode(of = "id")`
- Localized text uses `@ElementCollection` + `@CollectionTable` with `Map<String, String>` (locale -> text)
- Foreign keys on join tables use `@ForeignKey(name = "...")`

## Service Layer Patterns

### Single responsibility
- One service per domain entity (e.g., `PointOfInterestService`, `CreatorService`)
- Shared domain operations go into abstract parent (e.g., `AbstractCollectionService` for Collection add/remove operations)

### Transaction management
- `@Service`, `@Transactional(readOnly = true)` at class level
- Write methods get `@Transactional` override (makes them write-only)
- Use `logEnter(log)` / `logExit(log)` from `LogHelper` for method entry/exit tracing

### Business logic flow
1. Accept command object from `commands/`
2. Resolve references via dependency services (e.g., `creatorService.getByReference()`)
3. Validate (e.g., null checks) and throw `IllegalArgumentException` or `DataConstraintException`
4. Build entity using builder pattern
5. Persist via repository
6. Return domain entity

## Command Pattern (Input Objects)

- Plain `record` classes in `commands/` package
- Naming: `{Create|Update|Add|Remove}{Entity}Command`
- Use `@Builder` (not `@SuperBuilder`)
- Include `@NotNull`, `@Valid` for validation annotations
- Never extend other commands — each command is standalone

## DTO Pattern (Output Objects)

- Java `record` classes in `dto/` package
- Mirror domain entity structure but with resolved references (not lazy proxies)
- Use Lombok `@Builder` with custom inner builder class for default values (Java records don't support `@Builder.Default`)
- Names: `{Entity}Dto`

## Mapper Pattern (MapStruct)

### Configuration
- Central `SpringMapperConfig`: `@MapperConfig(componentModel = MappingConstants.ComponentModel.SPRING)`
- All mappers: `@Mapper(config = SpringMapperConfig.class, uses = { ... })`

### Mapper interface
- `toDto(Entity)` returns single DTO
- `toDtos(List<Entity>)` returns list of DTOs
- Configure `uses` attribute for nested mappers (e.g., `ContentMapper`, `CreatorMapper`)

### Naming convention
- `{Entity}Mapper` interface

## Repository Pattern

- `@Repository` annotation on Spring Data JPA interfaces
- Extends `JpaRepository<Entity, EntityId>`
- Use JPA entity projections for select queries (`findProjectedBy`, `findAllProjectedBy`)
- Named `@Query` for complex queries (e.g., full-text search on localized maps)
- One repository per entity

## Rich Type Pattern

### Structure
```java
// 1. Interface in richtypes/
public interface SingleValue<T> { T value(); }

// 2. Value object in richtypes/
@Embeddable
public record Duration(...) implements SingleValue<Integer> {
    // validation in constructor
}

// 3. Converter in persistence/converters/
public class DurationConverter extends AbstractIntegerRichTypeConverter<Duration> { ... }
// Register with @Converter or @Convert on entity field
```

### Purpose
Rich types encapsulate domain constraints (e.g., duration must be 0-250) and prevent invalid states at the type level.

## Presentation Layer

### Controller
- `@RestController`, `@RequiredArgsConstructor`, `@Slf4j`
- `@RequestMapping("/api/{plural}")` for the endpoint prefix
- Dependencies: one service + one model assembler
- Return `ResponseEntity<EntityModel<T>>` with HATEOAS links

### Model Assembler
- `@Component` implementing `RepresentationModelAssembler<Entity, EntityModel<Dto>>`
- `toModel(Entity)` converts domain entity -> DTO via mapper, wraps in `EntityModel`
- Adds `self` and `collection` links with affordances
- Use `linkTo(methodOn(...)).andAffordance(afford(...))` for allowed operations

### Paging
- Use `Slice` (not `Page`) for memory-efficient unbounded pagination
- Wrap in `SlicedModel` with `prev`/`next` HATEOAS links
- Use `Pageable` parameter injected by Spring MVC

### Error handling
- `GlobalControllerAdvice` with `@ExceptionHandler` returning `ProblemDetail` (RFC 7807)
- Validation errors handled manually in controller via `BindingResult`

### Exception handling in service layer
- `DataConstraintException` (in `foundation/`) — static factory methods:
  - `forUnmappedEnumValue()`, `forDuplicatedEntry()`, `forMissingEntry()`, `forCircularReference()`
- These are caught by `GlobalControllerAdvice` -> returns 500 (upgrade to proper status codes as needed)

## Test Patterns

### Controller tests
- Base class: `AbstractDocumentationControllerTest` with MockMvc + Spring REST Docs setup
- Each controller test extends base, uses `@WebMvcTest` or slice config
- Generates asciidoc snippets for API documentation

### Service tests
- Unit tests with mocked repositories/dependencies
- Use JavaFaker for test data generation
- Shared fixtures in `ServiceFixtures.java`
- One test class per service

### Repository tests
- Use Testcontainers (PostgreSQL + Mailpit)
- `@DataJpaTest` + `@ExtendWith(TestcontainersExtension.class)`
- Tests actual JPA behavior with real database

### Mapper tests
- Pure unit tests (no Spring context needed)
- `@ExtendWith(MapstructBenchmarkExtension.class)` or simple Mockito
- Verify all mapping methods for correctness

## Common Tasks

### Adding a new entity
1. Create domain entity class in `domain/` (extend `HistoryBase` or `AbstractEntity`)
2. Create `XxxDto` in `dto/`
3. Create `CreateXxxCommand`, `UpdateXxxCommand` in `commands/`
4. Create `XxxRepository` in `persistence/`
5. Create `XxxService` in `service/` (or add to existing service for small entities)
6. Create `XxxMapper` in `mappers/`
7. Create `XxxController` and `XxxModelAssembler` in `presentation/`
8. Add tests in corresponding `test/` subpackages

### Adding a rich type
1. Create `Record` implementing `SingleValue<T>` in `richtypes/`
2. Create `*Converter` extending `AbstractIntegerRichTypeConverter<T>` in `persistence/converters/`
3. Use `@Convert(converter = XxxConverter.class)` on entity field, or register globally

### Adding a new command
1. Create `record` in `commands/` with `@Builder`
2. Add validation annotations (`@NotNull`, `@Valid`, etc.)
3. Reference from service method
4. Add endpoint to controller if new operation

### Adding a repository query
1. Add method to repository interface
2. Use JPA projections for efficient selects: `<T> Optional<T> findProjectedBy(...)`
3. Use `@Query` with JPQL for complex queries (localized text queries, aggregations)
