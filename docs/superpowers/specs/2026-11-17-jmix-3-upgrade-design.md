# JMix 3.1.0 Upgrade + Database Removal — Design

## Intent

Transform `xvspellconverter` (Conversor de Grafia Xavante) into a **database-free
application** that keeps only the public orthography converter, running on the latest
stable framework: **JMix 3.1.0** (Spring Boot 4.1, Vaadin 25.3).

Why: the Postgres database (LAN `192.168.21.35`) is a deployment burden and unreachable
from dev machines; the only feature that matters publicly is the text conversion.
The dictionary of exception words moves into the app itself.

## Goals

- JMix 3.1.0; **zero database**: no Postgres driver, no EclipseLink/JPA, no Liquibase,
  no datasource properties — the app starts without any DB
- Public converter behaves exactly as today: letter rules (z→dz, s→ts, â→ö, i→ĩ,
  glottal stop, phantom space) **plus** dictionary exceptions (e.g. "Mozésihi"→"Moisés")
  served from an embedded resource file
- Anonymous-only: no login, no user management, no admin views
- Build and all tests pass on this machine with no external dependencies

## Non-goals / anti-goals

- No admin UI, no in-memory admin login (nothing to administer)
- No new JMix 3.x features (Aura theme, AI add-ons, runtime view templates)
- No refactors beyond what removal requires; the `app`/`infra` hexagonal layout stays

## Approaches considered

1. **Dictionary: embed as TSV resource (chosen)** — `GetWordGateway` port stays; new
   adapter loads ~558 words extracted from the repo's Liquibase seed. Behavior
   identical. Caveat accepted: words added via the admin UI after July 2024 exist only
   in the live DB and are not recoverable from here (user accepted this).
2. Dictionary: drop lookups entirely — rejected: changes real output for known names.
3. Framework: stay on JMix 2.8.6 — rejected (user chose latest 3.1.0).
4. Auth: in-memory admin login — rejected: no admin views remain to protect.

## The work, as decided

**Removals**
- `infra/entity/User.java`, `infra/entity/Word.java` (JPA); Liquibase resources and the
  `main.liquibase.change-log` property
- `infra/gateway/GetWordRepositoryGateway.java` (DB adapter)
- `infra/view/login/`, `infra/view/user/`, `infra/view/word/` (controllers, XML,
  messages) and the Login button + handler in `AnonymousMainViewTopMenu`
- `infra/security/DatabaseUserRepository.java` — replaced by an in-memory
  `UserRepository` providing only the anonymous/system principals
- `build.gradle`: `jmix-eclipselink-starter`, `jmix-security-data-starter`,
  `jmix-datatools-starter`, `jmix-datatools-flowui-starter`, `org.postgresql:postgresql`;
  MapStruct (declared but never used)
- `jmix.ui.login-view-id` property; `main.datasource.*` in dev/prod profiles
- `UserTest`, `UserUiTest` (test user management UI that no longer exists)

**Additions**
- `words.tsv` resource: `from<TAB>to` pairs parsed from the seed SQL; looked up
  case-sensitively, exactly as the old `PropertyCondition.equal("from", …)` did
- `GetWordDictionaryGateway` (`@Component`) implementing `GetWordGateway` from the TSV
- A minimal `SecurityFilterChain` permitting `/icons/**` (Vaadin 25 default-deny change;
  this app serves `/icons/icon.png` as PWA icon)

**JMix 3.1 upgrade mechanics** (replicating Studio's migration)
- Temurin JDK 21 installed user-locally (`~/.jdks`, no sudo needed)
- `io.jmix` plugin + BOM → `3.1.0`; Gradle wrapper → 9.8.0; MapStruct removed
- Keep: `jmix-core`, `jmix-flowui` (+data, +themes), `jmix-security` (+flowui),
  `jmix-flowui-test-assist`; add `com.vaadin:vaadin-dev`; skip the generic
  `spring-boot-jdbc-test` (no datasource exists)
- `vaadin` block: `productionMode = false`; app class: `@Stylesheet(Lumo.UTILITY_STYLESHEET)`,
  `@PWA(offline = false)`; new `vaadin-featureflags.properties`; `DataSourceProperties`
  bean and all datasource wiring deleted from `XvspellconverterApplication`
- `jmix-localfs-starter`: removed if no FileStorage usage is found (checked during build)
- After migration: `./gradlew clean vaadinClean`

**Tests**
- `ConvertUseCaseTest` becomes a plain unit test (no Spring context): constructs
  `ConvertUseCase` with `GetWordDictionaryGateway`; all existing assertions stay,
  including the dictionary cases ("Mozésihi"→"Moisés", "Imama"→"João")
- `GetWordRepositoryGatewayTest` merges into the above (dictionary adapter covered
  through `ConvertUseCase`)
- `AuthenticatedAsAdmin` helper removed with the security-data layer

## Verification

- Temurin 21 + `./gradlew clean vaadinClean build` compiles
- `./gradlew test` green (pure unit tests, no DB, no Spring context)
- Boot the app (no datasource configured at all), curl the root page: anonymous
  converter renders; `/icons/icon.png` returns 200

## Left to the builder

- Exact handling of any additional compile-time breakages surfaced by JMix 3.1
  (resolve per the official migration guide)
- Whether `jmix-localfs-starter` and `flowui-data` specifics need adjusting once the
  code compiles without the data layer
