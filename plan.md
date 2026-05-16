# Development Plan: Art CMS — Service, REST, Thymeleaf & Security

## I. Goal Overview

Complete the Art CMS teaching project by implementing three missing layers on top of the existing domain/persistence foundation: (1) missing REST controllers & DTOs/assemblers, (2) Spring Security with role-based access, and (3) a full Thymeleaf web interface. The final application exposes a HATEOAS-driven REST API *and* a browser-based UI, both secured by Spring Security with Creator-based authentication.

---

## II. Implementation Phases

### Phase 0: Foundation Fixes (DTOs, Mappers, Domain)

| Step | Description |
|------|-------------|
| **0.1** | **Add Creator password field** — `Creator` entity needs a `@NotBlank String password` + `@JsonIgnore`. Add Flyway migration `V1.0.2__AddCreatorPassword.sql`: `ALTER TABLE creator ADD COLUMN password varchar(255);`. Update `CreateCreatorCommand`/`UpdateCreatorCommand` to include `password`. |
| **0.2** | **Create missing DTOs** — `TextContentDto`, `ImageContentDto`, `VideoContentDto`, `SlideshowContentDto` (extend `ContentDto`); `CollectionDto`, `ExhibitionDto`; `VisitorDto`; `VisitHistoryDto`. |
| **0.3** | **Complete `ContentMapper`** — Add `@SubclassMapping` for all five content subtypes (currently only `AudioContent` is mapped). |
| **0.4** | **Create new mappers** — `CollectionMapper`, `VisitorMapper`, `VisitHistoryMapper`. |
| **0.5** | **Add `CreatorRepository.findByUsername(String)`** — Needed by Spring Security's `UserDetailsService`. |
| **0.6** | **Add `CreatorDto.role` field** — Currently missing; needed for role checks. |

### Phase 1: Complete REST API

| Step | Description |
|------|-------------|
| **1.1** | **Create model assemblers** — `CreatorModelAssembler`, `CollectionModelAssembler`, `ExhibitionModelAssembler`, `VisitorModelAssembler`, `VisitHistoryModelAssembler` in `presentation/assembler/`. |
| **1.2** | **Create missing REST controllers** — `CreatorController`, `CollectionController`, `ExhibitionController`, `VisitorController`, `VisitHistoryController`. Full CRUD (POST/GET/GET-all/PUT/PATCH/DELETE) for non-abstract entities. Pageable + sorting for Creator, Visitor, VisitHistory. |
| **1.3** | **Fix `ContentController`** — Implement `getContent(Long id)` (currently throws). Fix keyset pagination `prev`/`next` link logic. |
| **1.4** | **Add RestClient endpoint** — `GET /api/pois/{id}/comments` that calls `dummyjson.com/comments?limit=2&skip={random}&select=body` (random offset 0–98). |
| **1.5** | **Write REST controller tests** — One test class per controller extending `AbstractDocumentationControllerTest` with Spring REST Docs. |

### Phase 2: Spring Security

| Step | Description |
|------|-------------|
| **2.1** | **Create `CreatorDetails`** — `UserDetails` wrapper mapping `Creator.Role` to `GrantedAuthority` (`ROLE_VIEWER`, `ROLE_EDITOR`, `ROLE_ADMIN`). |
| **2.2** | **Create `CreatorUserDetailsService`** — `UserDetailsService` using `CreatorRepository.findByUsername()`. |
| **2.3** | **Create `SecurityConfig`** — `@Configuration @EnableWebSecurity` with `SecurityFilterChain`. Permit `/login`, `/register`, `/css/**`, `/js/**`. Require auth for everything else (including API). Form login with custom login page. |
| **2.4** | **Admin seed (`AdminInitializer`)** — `CommandLineRunner` that creates admin Creator (username=admin, password=admin, role=ADMIN) if not exists. |
| **2.5** | **Role-based access** — `@PreAuthorize` or request matchers: EDITOR/ADMIN can write, VIEWER read-only. |

### Phase 3: Thymeleaf Web Interface

| Step | Description |
|------|-------------|
| **3.1** | **Create layout template** — `templates/layout.html` with header, nav, footer. Nav reflects hierarchy. |
| **3.2** | **Create Thymeleaf controllers** — `CreatorWebController`, `CollectionWebController`, `ExhibitionWebController`, `PointOfInterestWebController`, `ContentWebController`, `VisitorWebController`, `VisitHistoryWebController` in `presentation.web` package. |
| **3.3** | **CRUD templates** — For each non-abstract entity: `list.html`, `detail.html`, `create.html`, `edit.html`, `confirm-delete.html`. Content: read-only list/detail. |
| **3.4** | **Navigation hierarchy** — Exhibition → POI → Content; Exhibition → Collection → POI. Implemented as breadcrumbs and navigable links. |
| **3.5** | **Content Pool page** — Lists unassigned content (not in any POI). "Create new content" button. |
| **3.6** | **POI Pool page** — Lists unassigned POIs (not in any collection). "Create new POI" button. |
| **3.7** | **Login / Registration** — `login.html` (form login), `register.html` (creates VIEWER Creator). |
| **3.8** | **Web access control** — All pages require auth. Visitor & VisitHistory: read-only (not createable via web). |

### Phase 4: Final Verification

| Step | Description |
|------|-------------|
| **4.1** | Run `./mvnw verify` — all service, controller, repository, mapper tests pass. |
| **4.2** | Manual smoke test — login, registration, CRUD flows, hierarchy, pools, REST API with HATEOAS. |

---

## III. Dependencies, Risks & Considerations

### Dependencies between phases
- **Phase 0** must complete before Phase 1 (DTOs/mappers needed by controllers).
- **Phase 2** can be developed in parallel with Phase 1, but must integrate before Phase 3 (Thymeleaf needs security).
- **Phase 3** depends on both Phase 1 (controller patterns) and Phase 2 (auth integration).
- Step **0.1** (Creator password) is a prerequisite for all of Phase 2.

### Risks
1. **Creator lacks password** — The entity, DB schema, and commands all need updating. Most critical gap for security.
2. **ContentController keyset pagination** — Current `prev`/`next` link computation uses arithmetic (`lastId - pageSize`) rather than tracking actual IDs from each slice. Needs rework.
3. **Flyway migration order** — `V1.0.2` must be compatible with existing data in dev databases.
4. **Content Pool query** — Requires JPQL: `SELECT c FROM Content c WHERE c NOT IN (SELECT key(p) FROM PointOfInterest p JOIN p.content c2 ...)` or similar.
5. **Exhibition/Collection inheritance in Thymeleaf** — `Exhibition extends Collection` shares the ID type; templates must handle both with care.
6. **API security scope** — Requirement says "nothing accessible without authentication". Decide if REST API also requires auth or if it stays open for mobile apps (contrast: Visitor/VisitHistory are mobile-created but those endpoints would then need auth too).

### Design considerations
- **Registration**: Creates `VIEWER`-role Creator; admin can promote via management UI.
- **VisitHistory.duration FIXME**: The existing `Duration` rich type caps at 250 seconds — unsuitable for visit durations. May need a separate rich type or schema change.
- **Thymeleaf reuse**: Use fragment-based layout to minimize duplication. Navigation hierarchy via partials.
- **REST API HATEOAS**: Follow the existing `PointOfInterestController` pattern for consistency across all new controllers.
- **Table name typo**: DB has `Exhibiton` (missing `i`) and `exhibiton` — consider leaving as-is or adding an alias migration.
