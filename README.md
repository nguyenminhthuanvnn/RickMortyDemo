# Rick & Morty Demo — Android Interview Project

A complete, runnable Android sample built to demonstrate modern Android development practices:

- **Clean Architecture** (domain / data / presentation, dependency inversion)
- **MVVM + MVI** (unidirectional `Intent -> State/Effect` contract)
- **100% Jetpack Compose** UI with Material 3 and **Pull-to-Refresh**
- **Offline-First Paging**: Paging 3 with `RemoteMediator` and Room cache
- **Cache Expiration Strategy**: 1-hour time-based invalidation for local data
- **Unit & Snapshot Tests**: JVM tests (MockK + Turbine) and **Paparazzi UI snapshots**
- **Hilt** for dependency injection
- **Advanced CI/CD**: Multi-workflow GitHub Actions for PR verification and Continuous Distribution
- **Fastlane**: Automated build, test, and **Firebase App Distribution** pipelines

## Screens

- **Character List**: A searchable, infinitely-scrolling list with Pull-to-Refresh support.
- **Character Detail**: Detailed view of a character including species, status, origin, and location.

## Architecture

```
presentation/          <- Compose UI + ViewModel (MVVM) + MVI contract
  characterlist/       State / Intent / Effect / Screen
  characterdetail/     State / Intent / Effect / Screen
  theme/               Material3 theme, color tokens
ui/components/          Reusable, dumb composables (CharacterItem, LoadStateFooter)

domain/                 <- Pure Kotlin, no Android/Retrofit/Compose deps
  model/Character.kt
  repository/CharacterRepository.kt   (Interface)
  usecase/GetCharactersUseCase.kt

data/                   <- Network + Local Persistence
  remote/CharacterApi.kt, dto/*.kt
  local/RickMortyDatabase.kt, dao/*.kt, entity/*.kt
  paging/CharacterRemoteMediator.kt   (Offline-first logic)
  repository/CharacterRepositoryImpl.kt
  mapper/CharacterMapper.kt           DTO -> Entity -> Domain model

di/                      Hilt modules (Network, Repository, Database)
```

### Offline-First Strategy
The app uses Room as the **Single Source of Truth**. The `RemoteMediator` manages data synchronization:
- **Initialization**: Checks a `lastUpdated` timestamp in the database. If the cache is older than **1 hour**, it triggers a fresh fetch from the network.
- **Resilience**: If the device is offline, the app continues to show cached data and provides a "Retry" option in the list footer when connectivity is restored.

## CI/CD: GitHub Actions

The project features a modular CI/CD setup:

1. **Unit Test on PR (`wf_unit_test_on_pr.yml`)**:
   - Runs on every PR opened/updated against `main`.
   - Executes `./gradlew lintStagingDebug` and `./gradlew testStagingDebugUnitTest`.
   - Performs **Paparazzi UI Snapshot verification** to prevent visual regressions.
   - Uploads HTML reports and snapshot failure diffs as artifacts.

2. **Continuous Distribution (`wf_distribution.yml`)**:
   - Runs automatically when code is merged into `main`.
   - Implements **Dynamic Versioning** using the `github.run_number`.
   - Decodes a secure production keystore from GitHub Secrets.
   - Builds and signs a `StagingRelease` APK.
   - Distributes the build to **Firebase App Distribution** for testers.

## Fastlane

```bash
bundle install                         # Install dependencies
bundle exec fastlane android lint       # Static analysis
bundle exec fastlane android test       # JVM unit tests
bundle exec fastlane android distribute_staging  # Build & Upload to Firebase
bundle exec fastlane android ci                 # Full pipeline (lint + test + build)
```

### Security & Signing
- **Keystores**: Signing keys are never committed. Locally, they are read from `local.properties`. In CI, they are reconstructed from Base64-encoded GitHub Secrets.
- **Service Accounts**: Firebase credentials are handled via secure environment variables and temporary JSON files on the CI runner.

## Tests included

| File | What it covers |
|---|---|
| `CharacterMapperTest` | DTO → Entity → Domain mapping logic |
| `CharacterPagingSourceTest` | Network-only pagination, error handling, search queries |
| `CharacterRepositoryImplTest` | Repository wiring with Database and API mocks |
| `CharacterListViewModelTest` | MVI contract: Intent → State/Effect transitions (Turbine) |
| `CharacterItemSnapshotTest` | **Paparazzi Snapshot**: Visual verification of UI components |

## Building & running

**Recommended:** Open in **Android Studio (Koala/2024.1+)**.

**Command line:**
```bash
# Generate wrapper if missing
gradle wrapper --gradle-version 8.9
# Build staging variant
./gradlew assembleStagingDebug
```

**Note:** For local release builds, ensure you have configured `RELEASE_KEYSTORE_PATH` in your `local.properties` or have a `rickykeystore-release.jks` in the root (it will fallback to debug signing if missing).
