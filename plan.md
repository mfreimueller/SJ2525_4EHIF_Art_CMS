# Development Plan: Email, File Upload, Actuator & Test Coverage

## I. Goal Overview

Extend the Art CMS project with three missing real-world features (email sending via Mailpit, file upload for content, and proper Actuator monitoring) while closing the major test gaps identified across all layers.

---

## II. Implementation Phases

### Phase A — Mailpit Integration for Email Sending

**Prerequisites:** `spring-boot-starter-mail` (pom.xml:86), `testcontainers-mailpit:1.3.1` (pom.xml:169) — both already declared, neither used.

| Step | Description | Files |
|------|-------------|-------|
| **A.1** | Create `EmailService` — Spring `@Service` with `JavaMailSender`. Method `sendEmail(to, subject, body)` builds `MimeMessage` with HTML content. Handle send errors gracefully (log + continue, never fail the caller). | `src/main/java/.../service/EmailService.java` |
| **A.2** | Create Mailpit test integration — Add `@ServiceConnection`-annotated `MailpitContainer` bean to `TestcontainersConfiguration.java` so tests auto-connect to Mailpit's SMTP (port 1025) and web API (port 8025). | `src/test/java/.../TestcontainersConfiguration.java` |
| **A.3** | Add `application-dev.properties` — Mailpit SMTP config: `spring.mail.host=localhost`, `spring.mail.port=1025`, `spring.mail.properties.mail.smtp.auth=false`. | `src/main/resources/application-dev.properties` |
| **A.4** | Wire email into registration — `WebAuthController.register()` calls `EmailService.sendWelcomeEmail(newCreator)` after successful registration (send asynchronously to avoid blocking the response). | `src/main/java/.../presentation/web/WebAuthController.java` |
| **A.5** | Wire email into content creation — `ImageContentService.create()` (and other content services) call `EmailService.sendNewContentNotification(adminEmail, content)` to notify admins of new content. | `src/main/java/.../service/ImageContentService.java` (+ other content services) |
| **A.6** | Create Thymeleaf email templates — `email/welcome.html` with `th:text` for recipient name, branded footer. | `src/main/resources/templates/email/welcome.html` |
| **A.7** | Write `EmailServiceTest` — `@SpringBootTest` with Mailpit `@ServiceConnection`. Sends email via service, then queries Mailpit REST API (`GET /api/v1/messages`) to verify subject/recipient/content. | `src/test/java/.../service/EmailServiceTest.java` |

### Phase B — File Upload (for ImageContent and other media)

**Rationale:** `ImageContent` has an `@Embedded Source(filePath, linkType)` but no mechanism to upload files. The `LinkType.Relative` enum value implies server-side storage was intended.

| Step | Description | Files |
|------|-------------|-------|
| **B.1** | Create upload configuration — `@ConfigurationProperties(prefix = "app.upload")` record with `Path dir` (default `uploads/`), `long maxFileSize` (default 10MB). Configure multipart properties in `application-dev.properties`. | `src/main/java/.../foundation/UploadProperties.java` |
| **B.2** | Create `FileStorageService` — `@Service` with methods: `store(MultipartFile) -> String filename` (UUID-based, validates extension), `load(String filename) -> Resource` (returns `UrlResource`), `delete(String filename)`. Handle: empty file, unsupported extension, not-found. | `src/main/java/.../service/FileStorageService.java` |
| **B.3** | Add REST upload endpoint to `ImageContentController` — `POST /api/content/image/{id}/upload` accepting `MultipartFile`. Calls `FileStorageService.store()`, updates `ImageContent.source` with relative path + `LinkType.Relative`. Return 201 with file URL. | `src/main/java/.../presentation/ImageContentController.java` |
| **B.4** | Serve uploaded files — `GET /api/files/{filename:.+}` returns `Resource` from `FileStorageService.load()` with proper `Content-Type` from `MediaTypeFactory`. | `src/main/java/.../presentation/FileController.java` |
| **B.5** | Add Thymeleaf upload form — Add file input to `content/detail.html` (visible for EDITOR/ADMIN). On upload, show preview link. | `src/main/resources/templates/content/detail.html` |
| **B.6** | Add Flyway migration `V1.0.3__CreateFileUpload.sql` — Optional: create `file_metadata` table to track original filename, size, content type, upload timestamp. | `src/main/resources/db/migration/V1.0.3__CreateFileUpload.sql` |
| **B.7** | Write `FileStorageServiceTest` — `@ExtendWith(MockitoExtension.class)` with `@TempDir Path uploadDir`. Tests: store valid file (returns UUID filename), store empty file (throws), delete existing file, delete non-existent (no-op). | `src/test/java/.../service/FileStorageServiceTest.java` |
| **B.8** | Write `FileUploadControllerTest` — `@WebMvcTest(FileController.class)` with `MockMultipartFile`, mock `FileStorageService`. Tests: upload success 201, download existing file 200, download missing 404. | `src/test/java/.../presentation/FileControllerTest.java` |

### Phase C — Actuator Configuration

**Status:** `spring-boot-starter-actuator` in pom.xml (line 63), but zero configuration exists beyond defaults.

| Step | Description | Files |
|------|-------------|-------|
| **C.1** | Configure actuator endpoints — `management.endpoints.web.exposure.include=health,info,metrics,prometheus,env`. Enable Kubernetes probes: `management.endpoint.health.probes.enabled=true`. Add app info via `info.*` properties. | `src/main/resources/application.properties` |
| **C.2** | Create `DatabaseHealthIndicator` — Custom `HealthIndicator` that executes `SELECT 1`, returns `UP` with database metadata. | `src/main/java/.../foundation/DatabaseHealthIndicator.java` |
| **C.3** | Create `ExternalApiHealthIndicator` — Pings dummyjson.com (used in `PointOfInterestController.getComments()`). Returns `UP` if reachable, `DOWN` with timeout info. Wrap in `try-catch` for resilience. | `src/main/java/.../foundation/ExternalApiHealthIndicator.java` |
| **C.4** | Add custom metrics — Inject `MeterRegistry` into `FileStorageService` (counter: `files.uploaded`, `files.deleted`; distribution summary: `file.size.bytes`). Add timer to `EmailService` (`email.send.duration`). Use Micrometer annotations for service-layer metrics. | `FileStorageService.java`, `EmailService.java` |
| **C.5** | Add Prometheus support — Ensure `micrometer-registry-prometheus` is in `pom.xml`. Verify `/actuator/prometheus` returns metrics in text format. | `pom.xml` |
| **C.6** | Secure actuator endpoints — In `SecurityConfig`: permit `/actuator/health` and `/actuator/health/*` for all, require ADMIN for `/actuator/env`, `/actuator/metrics`, `/actuator/prometheus`. | `src/main/java/.../security/SecurityConfig.java` |
| **C.7** | Write `ActuatorHealthTest` — `@SpringBootTest` with `TestRestTemplate`. Verify `/actuator/health` returns 200 with `{"status":"UP"}`, `/actuator/info` returns 200, authenticated `/actuator/env` returns 200 for admin. | `src/test/java/.../presentation/ActuatorHealthTest.java` |
| **C.8** | Write `HealthIndicatorTest` — Unit tests for `DatabaseHealthIndicator` and `ExternalApiHealthIndicator` with mocked dependencies. Verify UP/DOWN states. | `src/test/java/.../foundation/DatabaseHealthIndicatorTest.java` |

### Phase D — Closing Test Gaps

**Current coverage estimate:** ~55–65% line coverage. Major gaps: web controllers (0%), mappers (29%), security (0%), CreatorController (missing).

#### D.1 — Add JaCoCo (mandatory before all other test work)

| Step | Description | Files |
|------|-------------|-------|
| **D.1.1** | Add `jacoco-maven-plugin` to `<build><plugins>`. Configure `prepare-agent` + `report` goals. | `pom.xml` |
| **D.1.2** | Configure coverage rule: minimum line coverage 0.70, fail build if below. | `pom.xml` (jacoco rules) |

#### D.2 — Add Missing REST Controller Test

| Step | Description | Files |
|------|-------------|-------|
| **D.2.1** | Create `CreatorControllerTest` — Extends `AbstractDocumentationControllerTest`. Tests: POST (201), GET /{key} (200/404), GET paged (200), PUT (200/404), PATCH (200/404), DELETE (204/404). Uses `@MockitoBean` for `CreatorService`, `@MockitoSpyBean` for mapper + assembler. Generates REST Docs snippets. | `src/test/java/.../presentation/CreatorControllerTest.java` |

#### D.3 — Missing Mapper Tests (4 new files)

| Step | Description | Files |
|------|-------------|-------|
| **D.3.1** | `CreatorMapperTest` — `@SpringBootTest`, autowire `CreatorMapper`. Build `Creator` entity via fixture, verify `toDto()` maps `id`, `username`, `role`. | `src/test/java/.../mapper/CreatorMapperTest.java` |
| **D.3.2** | `CollectionMapperTest` — Build `Collection` with localized titles, POIs, subcollections. Verify `toDto()` maps nested `PointOfInterestDto[]` and subcollection refs. | `src/test/java/.../mapper/CollectionMapperTest.java` |
| **D.3.3** | `VisitorMapperTest` — Build `Visitor` with email, verify `toDto()` maps all scalar fields. | `src/test/java/.../mapper/VisitorMapperTest.java` |
| **D.3.4** | `VisitHistoryMapperTest` — Build `VisitHistory` with duration, visitedOn, POIs, Visitor. Verify nested objects map correctly. | `src/test/java/.../mapper/VisitHistoryMapperTest.java` |

#### D.4 — Web Controller Tests (8 new test classes)

**Base class pattern:**

`AbstractWebControllerTest` — `@SpringBootTest` + `@AutoConfigureMockMvc`. Includes `@WithMockUser(roles = "ADMIN")` for authenticated requests. Provides helper methods for form POST.

| Step | Description | Files |
|------|-------------|-------|
| **D.4.1** | Create `AbstractWebControllerTest` — Base class: `MockMvc` autowired, `@WithMockUser` set, form-data helpers. | `src/test/java/.../presentation/web/AbstractWebControllerTest.java` |
| **D.4.2** | `PoiWebControllerTest` — list (200, correct model), detail, create form (GET renders form), create POST (redirect + persists), edit form, edit POST, delete POST, pool page. | `src/test/java/.../presentation/web/PoiWebControllerTest.java` |
| **D.4.3** | `ContentWebControllerTest` — list, detail, pool page (unassigned content shown). No create/edit (read-only via web). | `src/test/java/.../presentation/web/ContentWebControllerTest.java` |
| **D.4.4** | `CreatorWebControllerTest` — list, detail, create, edit, delete; verify role display. | `src/test/java/.../presentation/web/CreatorWebControllerTest.java` |
| **D.4.5** | `CollectionWebControllerTest` — list, detail (POIs + subcollections visible), create, edit, delete, add/remove POI, add/remove subcollection. | `src/test/java/.../presentation/web/CollectionWebControllerTest.java` |
| **D.4.6** | `ExhibitionWebControllerTest` — list, detail (languages displayed), create (with language selection), edit, delete, add/remove POI, add/remove subcollection. | `src/test/java/.../presentation/web/ExhibitionWebControllerTest.java` |
| **D.4.7** | `VisitorWebControllerTest` — list, detail. No create/edit (read-only). | `src/test/java/.../presentation/web/VisitorWebControllerTest.java` |
| **D.4.8** | `VisitHistoryWebControllerTest` — list, detail. No create/edit (read-only). | `src/test/java/.../presentation/web/VisitHistoryWebControllerTest.java` |
| **D.4.9** | `WebAuthControllerTest` — GET `/login` (form renders), GET `/register` (form renders), POST `/register` valid (redirect), POST `/register` password mismatch (form with error). | `src/test/java/.../presentation/web/WebAuthControllerTest.java` |

#### D.5 — Security Tests

| Step | Description | Files |
|------|-------------|-------|
| **D.5.1** | `CreatorUserDetailsServiceTest` — `@ExtendWith(MockitoExtension.class)`. Mocks `CreatorRepository`. Tests: found returns `CreatorDetails`, not-found throws `UsernameNotFoundException`. | `src/test/java/.../security/CreatorUserDetailsServiceTest.java` |
| **D.5.2** | `SecurityConfigIntegrationTest` — `@SpringBootTest` + `TestRestTemplate`. Tests: unauthenticated → redirect to login, admin auth → 200 on `/web/pois`, POST `/api/pois` without auth → 401/403. | `src/test/java/.../security/SecurityConfigIntegrationTest.java` |
| **D.5.3** | `AdminInitializerTest` — `@SpringBootTest`. Verifies admin Creator exists after context start (username=admin, role=ADMIN). | `src/test/java/.../foundation/AdminInitializerTest.java` |

#### D.6 — Final Verification

| Step | Description |
|------|-------------|
| **D.6.1** | Run `./mvnw clean verify` — all 142+ existing + ~25 new tests pass, JaCoCo report generated. |
| **D.6.2** | Review JaCoCo report at `target/site/jacoco/index.html` for uncovered hotspots. Target: ≥70% line coverage. |
| **D.6.3** | Run `./mvnw spring-boot:run` with dev profile — manual smoke test: upload a file, verify email sent via Mailpit UI, check `/actuator/health` returns UP. |

---

## III. Estimated Impact

| Metric | Before | After |
|--------|--------|-------|
| Test classes | 35 | **~60** (+25) |
| Mapper test coverage | 29% (2/7) | **86%** (6/7) |
| REST controller test coverage | 86% (6/7) | **100%** (7/7) |
| Web controller test coverage | 0% (0/8) | **100%** (8/8) |
| Security test coverage | 0% | **covered** |
| Estimated line coverage | ~55–65% | **~75–85%** |
| New features | — | Email sending, file upload, health monitoring |

## IV. File Change Summary

| Scope | Files |
|-------|-------|
| **New main sources** | `EmailService.java`, `UploadProperties.java`, `FileStorageService.java`, `ImageContentController.java`, `FileController.java`, `DatabaseHealthIndicator.java`, `ExternalApiHealthIndicator.java` |
| **Modified main sources** | `WebAuthController.java`, `ImageContentService.java` (+ other content services), `SecurityConfig.java`, `application.properties`, `application-dev.properties`, `content/detail.html`, `ImageContentService.java` |
| **New test sources** | ~25 new test classes across service/, mapper/, presentation/, presentation/web/, security/, foundation/ |
| **Infrastructure** | `pom.xml` (JaCoCo + micrometer-registry-prometheus), `TestcontainersConfiguration.java` (MailpitContainer) |
