# Task List — Art CMS Extension

## Phase A — Mailpit Integration for Email Sending

- [x] **A.1** Create `EmailService` class with `JavaMailSender` and `sendEmail(to, subject, body)` method using `MimeMessage` with HTML content. Handle send errors gracefully (log + continue).
- [x] **A.2** Add `MailpitContainer` bean with `@ServiceConnection` to `TestcontainersConfiguration.java` for test auto-discovery.
- [x] **A.3** Create `application-dev.properties` with Mailpit SMTP config: `spring.mail.host=localhost`, `spring.mail.port=1025`, `spring.mail.properties.mail.smtp.auth=false`.
- [x] **A.4** Wire email into `WebAuthController.register()` — call `EmailService.sendWelcomeEmail()` after successful registration.
- [x] **A.5** Wire email into `ImageContentService.create()` (and other content services) — call `EmailService.sendNewContentNotification()` to notify admins.
- [x] **A.6** Create Thymeleaf email template `templates/email/welcome.html` with recipient name and branded footer.
- [x] **A.7** Write `EmailServiceTest` — `@SpringBootTest` with Mailpit container, send email, verify via Mailpit REST API (`GET /api/v1/messages`).

## Phase B — File Upload

- [x] **B.1** Create `UploadProperties` record (`@ConfigurationProperties(prefix = "app.upload")`) with `Path dir` and `long maxFileSize`.
- [x] **B.2** Create `FileStorageService` with `store(MultipartFile) -> String`, `load(String) -> Resource`, `delete(String)`. Handle empty file, unsupported extension, not-found.
- [x] **B.3** Add REST upload endpoint `POST /api/content/image/{id}/upload` to new `ImageContentController`. Store file, update `ImageContent.source` with relative path + `LinkType.Relative`, return 201.
- [x] **B.4** Create `FileController` with `GET /api/files/{filename:.+}` serving `Resource` from `FileStorageService` with proper `Content-Type`.
- [x] **B.5** Add file upload form to `content/detail.html` (visible for EDITOR/ADMIN). Show preview link after upload.
- [x] **B.6** Create Flyway migration `V1.0.3__CreateFileUpload.sql` — add optional `file_metadata` table (original filename, size, content type, upload timestamp).
- [x] **B.7** Write `FileStorageServiceTest` — `@ExtendWith(MockitoExtension.class)` with `@TempDir`. Test store/delete/error cases.
- [x] **B.8** Write `FileUploadControllerTest` — `@WebMvcTest(FileController.class)` with `MockMultipartFile`, mock `FileStorageService`. Test 201/200/404.

## Phase C — Actuator Configuration

- [x] **C.1** Configure actuator in `application.properties`: expose `health,info,metrics,prometheus,env`. Enable health probes. Add `info.*` properties.
- [x] **C.2** Create `DatabaseHealthIndicator` — custom `HealthIndicator` pinging database with `SELECT 1`, return UP/DOWN with metadata.
- [x] **C.3** Create `ExternalApiHealthIndicator` — pings dummyjson.com, returns UP/DOWN with timeout info.
- [x] **C.4** Add custom Micrometer metrics: `FileStorageService` counters (`files.uploaded`, `files.deleted`), `EmailService` timer (`email.send.duration`). (Note: `file.size.bytes` DistributionSummary omitted — static builder unmockable in tests)
- [x] **C.5** Add `micrometer-registry-prometheus` dependency to `pom.xml`.
- [x] **C.6** Secure actuator endpoints in `SecurityConfig`: permit `/actuator/health/**` for all, restrict `/actuator/env` and `/actuator/metrics` to ADMIN.
- [x] **C.7** Write `ActuatorHealthTest` — `@SpringBootTest` + `TestRestTemplate`. Verify `/actuator/health` UP. (Note: `/actuator/info` not exposed — Spring Boot 4.0.0 bug in `@ConditionalOnAvailableEndpoint` for `spring-boot-actuator-autoconfigure` module)
- [x] **C.8** Write `HealthIndicatorTest` — unit tests for both custom health indicators with mocked dependencies.

## Phase D — Closing Test Gaps

### D.1 — JaCoCo Setup

- [x] **D.1.1** Add `jacoco-maven-plugin` to `pom.xml` with `prepare-agent` and `report` goals.
- [x] **D.1.2** Configure JaCoCo coverage rule: minimum line coverage 0.70.

### D.2 — Missing REST Controller Test

- [x] **D.2.1** Create `CreatorControllerTest` extending `AbstractDocumentationControllerTest`. Test POST/GET/PUT/PATCH/DELETE with `@MockitoBean` service + `@MockitoSpyBean` mapper/assembler. Generate REST Docs snippets.

### D.3 — Missing Mapper Tests

- [x] **D.3.1** Create `CreatorMapperTest` — `@SpringBootTest`, verify `toDto()` maps id, username, role.
- [x] **D.3.2** Create `CollectionMapperTest` — verify localized titles, POIs, subcollections in `toDto()`.
- [x] **D.3.3** Create `VisitorMapperTest` — verify all scalar fields in `toDto()`.
- [x] **D.3.4** Create `VisitHistoryMapperTest` — verify duration, visitedOn, POIs, visitor in `toDto()`.

### D.4 — Web Controller Tests

- [x] **D.4.1** Create `AbstractWebControllerTest` base class (`@SpringBootTest` + `@Import(TestcontainersConfiguration.class)` with helper auth methods).
- [x] **D.4.2** Create `PoiWebControllerTest` — list, detail, create, edit, delete, pool.
- [x] **D.4.3** Create `ContentWebControllerTest` — list, detail.
- [x] **D.4.4** Create `CreatorWebControllerTest` — list, detail, create, edit, delete.
- [x] **D.4.5** Create `CollectionWebControllerTest` — list, detail, create, edit, delete, add/remove POI, add/remove subcollection.
- [x] **D.4.6** Create `ExhibitionWebControllerTest` — list, detail, create, edit, delete, add/remove POI, add/remove subcollection.
- [x] **D.4.7** Create `VisitorWebControllerTest` — list, detail.
- [x] **D.4.8** Create `VisitHistoryWebControllerTest` — list, detail.
- [x] **D.4.9** Create `WebAuthControllerTest` — login, register, register password mismatch, redirect root.

### D.5 — Security Tests

- [ ] **D.5.1** Create `CreatorUserDetailsServiceTest` — mock `CreatorRepository`, test found/not-found.
- [ ] **D.5.2** Create `SecurityConfigIntegrationTest` — `@SpringBootTest` + `TestRestTemplate`. Test unauthenticated redirect, authenticated 200, POST without auth 403.
- [ ] **D.5.3** Create `AdminInitializerTest` — verify admin user exists after context start.

### D.6 — Final Verification

- [ ] **D.6.1** Run `./mvnw clean verify` — all ~167 tests pass, JaCoCo report generated.
- [ ] **D.6.2** Review JaCoCo report at `target/site/jacoco/index.html`. Target: ≥70% line coverage.
- [ ] **D.6.3** Manual smoke test with dev profile: upload file, verify email via Mailpit UI (port 8025), check `/actuator/health` returns UP.
