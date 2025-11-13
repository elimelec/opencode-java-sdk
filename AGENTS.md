# Repository Guidelines

## Project Structure & Module Organization
Core SDK code lives in `src/main/java/dev/sst/opencode`, split by domain (`client`, `services`, `spring`, `models`, `config`, etc.). Shared configuration resources belong in `src/main/resources`. Tests mirror this layout under `src/test/java`; quick unit suites run in `dev/sst/opencode`, while slower end-to-end fixtures live in `dev/sst/opencode/example`. Reference apps (`examples/spring-chat-app`, `examples/openai-api-bridge`) and container assets (`Dockerfile`, `docker-compose.yml`) support integration and distribution scenarios.

## Build, Test, and Development Commands
Use Maven 3.9+ with Java 24 preview. `mvn clean install` builds the SDK, runs tests, and publishes to the local Maven repo for the examples. `mvn test` is the fastest feedback loop; add `-DskipTests` only during compilation debugging. Re-enable targeted integration cases by removing `@Disabled` or wiring a profile and call `mvn verify -Pintegration`. `docker-compose up --build` orchestrates the SDK, chat demo, and API bridge for manual smoke checks.

## Coding Style & Naming Conventions
Follow standard Java style: 4-space indentation, UTF-8 sources, and package names anchored at `dev.sst.opencode`. Classes and enums use PascalCase, methods and fields use camelCase, and tests end in `*Test`. Prefer Lombok builders already in the models, keep public APIs null-safe, and rely on SLF4J for logging. Validate inbound data with Jakarta bean validation annotations when exposing new configuration.

## Testing Guidelines
Write JUnit 5 tests alongside the code they exercise and mock remote services with OkHttp `MockWebServer` where feasible. Clearly document integration-only paths with expressive `@Disabled` messages so CI remains stable. Name methods after behavior (`createsAgentWhenRequestValid`) and verify both payload shape and side effects. Update example apps or docker flows when tests surface user-visible changes.

## Commit & Pull Request Guidelines
Adopt the prevailing conventional commits style (`feat:`, `fix:`, `docs:`, `chore:`) and keep the subject focused on the behavior change. Run `mvn clean install` plus any relevant docker-compose checks before opening a PR. Include linked issues, outline manual verification steps, note breaking changes, and add screenshots or logs when altering the example apps or developer tooling.

## Docker & Environment Notes
Use the root `Dockerfile` for SDK validation and `docker-compose.yml` to spin up the bridge and chat samples. Configure runtime via environment variables such as `OPENCODE_URL` and `API_SECURITY_ENABLED` (documented in `DOCKER.md`). Store credentials in local `.env` files or Docker secrets—never commit secrets to the repository.
