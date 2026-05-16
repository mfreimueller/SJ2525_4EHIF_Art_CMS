# Task List: Art CMS Development

## Phase 0: Foundation Fixes

- [x] **0.1** Add `password` field (`@NotBlank String password`, `@JsonIgnore`) to `Creator` entity
- [x] **0.2** Create Flyway migration `V1.0.2__AddCreatorPassword.sql` adding `password` column to `creator` table
- [x] **0.3** Add `password` field to `CreateCreatorCommand` record
- [x] **0.4** Add `password` field to `UpdateCreatorCommand` record
- [x] **0.5** Create `TextContentDto` (extends `ContentDto`) with `shortText`, `longText` fields
- [x] **0.6** Create `ImageContentDto` (extends `ContentDto`) with `source` field
- [x] **0.7** Create `VideoContentDto` (extends `ContentDto`) with `source`, `duration`, `subtitles` fields
- [x] **0.8** Create `SlideshowContentDto` (extends `ContentDto`) with `slides`, `mode`, `speed` fields
- [x] **0.9** Create `CollectionDto` with title, pointsOfInterest, subCollections, parentCollection, createdAt, updatedAt, createdBy, updatedBy
- [x] **0.10** Create `ExhibitionDto` (extends `CollectionDto`) with `languages` field
- [x] **0.11** Create `VisitorDto` with id, username, emailAddress
- [x] **0.12** Create `VisitHistoryDto` with id, duration, visitedOn, pointsOfInterest, visitor
- [x] **0.13** Add `@SubclassMapping` entries to `ContentMapper` for `TextContent`, `ImageContent`, `VideoContent`, `SlideshowContent`
- [x] **0.14** Create `CollectionMapper` interface (with `toDto`, `toDtos` methods)
- [x] **0.15** Create `VisitorMapper` interface
- [x] **0.16** Create `VisitHistoryMapper` interface
- [x] **0.17** Add `findByUsername(String username)` method to `CreatorRepository`
- [x] **0.18** Add `role` field to `CreatorDto`

## Phase 1: Complete REST API

- [x] **1.1** Create `CreatorModelAssembler` with self link + affordances for PUT, PATCH, DELETE
- [x] **1.2** Create `CollectionModelAssembler` with self link + affordances
- [x] **1.3** Create `ExhibitionModelAssembler` with self link + affordances
- [x] **1.4** Create `VisitorModelAssembler` with self link + affordances
- [x] **1.5** Create `VisitHistoryModelAssembler` with self link + affordances
- [x] **1.6** Create `CreatorController` (`/api/creators`) with POST, GET, GET-all (pageable), PUT, PATCH, DELETE
- [x] **1.7** Create `CollectionController` (`/api/collections`) with POST, GET, GET-all, PUT, PATCH, DELETE, add/remove POI, add/remove subCollection
- [x] **1.8** Create `ExhibitionController` (`/api/exhibitions`) with POST, GET, GET-all, PUT, PATCH, DELETE, add/remove POI, add/remove subCollection
- [x] **1.9** Create `VisitorController` (`/api/visitors`) with POST, GET, GET-all (pageable), PUT, PATCH, DELETE
- [x] **1.10** Create `VisitHistoryController` (`/api/visit-histories`) with POST, GET, GET-all (pageable)
- [x] **1.11** Implement `ContentController.getContent(Long id)` (replace `NotImplementedException` with actual logic)
- [x] **1.12** Fix keyset pagination `prev`/`next` link computation in `ContentController.getContents()`
- [x] **1.13** Add `GET /api/pois/{id}/comments` endpoint to `PointOfInterestController` using `RestClient` to fetch 2 random comments from `dummyjson.com`
- [x] **1.14** Write `PointOfInterestControllerTest`
- [x] **1.15** Write `CollectionControllerTest`
- [x] **1.16** Write `ExhibitionControllerTest`
- [x] **1.17** Write `VisitorControllerTest`
- [x] **1.18** Write `VisitHistoryControllerTest`
- [x] **1.19** Write/update `ContentControllerTest` covering `getContent` and keyset pagination

## Phase 2: Spring Security

- [ ] **2.1** Create `CreatorDetails` class implementing `UserDetails` wrapping `Creator`, mapping `Role` to `GrantedAuthority`
- [ ] **2.2** Create `CreatorUserDetailsService` implementing `UserDetailsService`, delegating to `CreatorRepository.findByUsername()`
- [ ] **2.3** Create `SecurityConfig` with `@EnableWebSecurity` and `SecurityFilterChain` bean:
  - Permit `/login`, `/register`, `/css/**`, `/js/**`
  - Require authentication for all other paths (including `/api/**`)
  - Configure form login with custom login page
- [ ] **2.4** Create `AdminInitializer` (`CommandLineRunner`) that seeds admin Creator (username=admin, password=admin, role=ADMIN) if not exists
- [ ] **2.5** Configure role-based authorization: EDITOR/ADMIN for write operations, VIEWER for read-only

## Phase 3: Thymeleaf Web Interface

- [ ] **3.1** Create `templates/layout.html` with Thymeleaf fragments (header, nav, sidebar, footer)
- [ ] **3.2** Create `templates/login.html` with login form
- [ ] **3.3** Create `templates/register.html` with registration form (creates VIEWER Creator)
- [ ] **3.4** Create `PointOfInterestWebController` with list, detail, create, edit, delete handlers
- [ ] **3.5** Create POI templates: `poi/list.html`, `poi/detail.html`, `poi/create.html`, `poi/edit.html`, `poi/confirm-delete.html`
- [ ] **3.6** Create `ContentWebController` with list, detail handlers (read-only)
- [ ] **3.7** Create Content templates: `content/list.html`, `content/detail.html` (read-only)
- [ ] **3.8** Create `CreatorWebController` with list, detail, create, edit, delete handlers
- [ ] **3.9** Create Creator templates: `creator/list.html`, `creator/detail.html`, `creator/create.html`, `creator/edit.html`, `creator/confirm-delete.html`
- [ ] **3.10** Create `CollectionWebController` with list, detail, create, edit, delete, add/remove POI, add/remove subCollection
- [ ] **3.11** Create Collection templates: `collection/list.html`, `collection/detail.html`, `collection/create.html`, `collection/edit.html`, `collection/confirm-delete.html`
- [ ] **3.12** Create `ExhibitionWebController` with list, detail, create, edit, delete, add/remove POI, add/remove subCollection
- [ ] **3.13** Create Exhibition templates: `exhibition/list.html`, `exhibition/detail.html`, `exhibition/create.html`, `exhibition/edit.html`, `exhibition/confirm-delete.html`
- [ ] **3.14** Create `VisitorWebController` with list, detail handlers (read-only, no create/edit)
- [ ] **3.15** Create Visitor templates: `visitor/list.html`, `visitor/detail.html`
- [ ] **3.16** Create `VisitHistoryWebController` with list, detail handlers (read-only, no create/edit)
- [ ] **3.17** Create VisitHistory templates: `visit-history/list.html`, `visit-history/detail.html`
- [ ] **3.18** Implement nav hierarchy: Exhibition index shows linked POIs and Collections; POI detail shows linked Content; Collection detail shows parent Exhibition + linked POIs
- [ ] **3.19** Create Content Pool page listing all Content not assigned to any POI, with "create new" button
- [ ] **3.20** Create POI Pool page listing all POIs not assigned to any Collection, with "create new" button
- [ ] **3.21** Add repository queries: find unassigned Content, find unassigned POIs

## Phase 4: Final Verification

- [ ] **4.1** Run `./mvnw verify` and fix any test failures
- [ ] **4.2** Manual smoke test: start app, verify login/registration, CRUD flows, navigation hierarchy, pool pages, REST API HATEOAS links
