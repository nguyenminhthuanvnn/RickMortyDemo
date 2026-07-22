# Rick & Morty Demo — Android Interview Project

A complete, runnable Android sample built to demonstrate:

- **Clean Architecture** (domain / data / presentation, dependency inversion)
- **MVVM** (`StateFlow` exposed by `ViewModel`, collected by Compose)
- **MVI** (unidirectional `Intent -> State/Effect` contract on top of MVVM)
- **100% Jetpack Compose** UI, Material 3
- **Remote data + infinite-scroll paging** via Paging 3, backed by the free,
  no-auth [Rick and Morty API](https://rickandmortyapi.com/)
- **Unit tests** (JVM, MockK + Turbine + Truth) and a **Compose UI test**
- **Hilt** for dependency injection
- **GitHub Actions** CI (lint, unit tests, Fastlane lane, debug APK, optional
  instrumented tests on an emulator)
- **Fastlane** lanes for local/CI automation and Play Store deployment

## Screens

One screen: a searchable, infinitely-scrolling list of Rick & Morty
characters. Scrolling near the bottom triggers Paging 3 to fetch the next
page automatically; a footer spinner/retry row shows load progress or
errors.

## Architecture

```
presentation/          <- Compose UI + ViewModel (MVVM) + MVI contract
  characterlist/
    CharacterListContract.kt   State / Intent / Effect
    CharacterListViewModel.kt  StateFlow<State>, onIntent(), Flow<PagingData>
    CharacterListScreen.kt     Stateful screen + stateless Content composable
  theme/                Material3 theme, color tokens
ui/components/          Reusable, dumb composables (CharacterItem, LoadStateFooter)

domain/                 <- Pure Kotlin, no Android/Retrofit/Compose deps
  model/Character.kt
  repository/CharacterRepository.kt   (interface — dependency inversion point)
  usecase/GetCharactersUseCase.kt

data/                   <- Everything that knows about the network
  remote/CharacterApi.kt, dto/*.kt
  paging/CharacterPagingSource.kt     (drives "scroll to load more")
  repository/CharacterRepositoryImpl.kt
  mapper/CharacterMapper.kt           DTO -> domain model

di/                      Hilt modules (Network, Repository binding)
```

Dependency rule: `presentation -> domain <- data`. The domain layer defines
`CharacterRepository` as an interface; `data` implements it; `presentation`
only ever talks to the interface and to `GetCharactersUseCase`. Swapping the
backend (e.g. GraphQL, a local Room cache, etc.) never touches
`presentation`.

### MVI contract

```kotlin
data class CharacterListState(val searchQuery: String = "", val isSearchBarVisible: Boolean = false)

sealed interface CharacterListIntent {
    data class OnSearchQueryChanged(val query: String) : CharacterListIntent
    data object OnToggleSearchBar : CharacterListIntent
    data object OnClearSearch : CharacterListIntent
    data class OnCharacterClicked(val characterId: Int) : CharacterListIntent
}

sealed interface CharacterListEffect {
    data class ShowMessage(val message: String) : CharacterListEffect
    data class NavigateToDetail(val characterId: Int) : CharacterListEffect
}
```

The Composable never mutates state directly — it only calls
`onIntent(...)`. The ViewModel is the single source of truth
(`StateFlow<CharacterListState>`) and emits one-off `Effect`s (snackbars/nav)
through a `Channel`, so they aren't redelivered on rotation. Paging data is
modeled as its own `Flow<PagingData<Character>>` (the idiomatic Paging 3 +
Compose pattern) rather than embedded in `State`, since `PagingData` is
already a stream designed to be collected independently via
`collectAsLazyPagingItems()`.

### Paging / infinite scroll

`CharacterPagingSource` calls the Rick & Morty API page by page; the "next
page" key comes straight from the API's own `info.next` pagination link.
`CharacterRepositoryImpl` wraps it in a `Pager` with `cachedIn(viewModelScope)`
so the list survives configuration changes. The Compose screen calls
`LazyColumn` with `pagingItems.itemCount`, which is what makes scrolling
toward the bottom trigger the next `load()` call automatically — no manual
"reached bottom" detection needed.

## Tests included

| File | What it covers |
|---|---|
| `CharacterMapperTest` | DTO → domain mapping, status parsing |
| `CharacterPagingSourceTest` | first page, last page, IOException → `LoadResult.Error`, search query passthrough |
| `CharacterRepositoryImplTest` | repository wiring |
| `GetCharactersUseCaseTest` | use case delegates correctly to the repository |
| `CharacterListViewModelTest` | MVI: every Intent → expected State, debounced search triggers a new use-case call (MockK + Turbine) |
| `CharacterListContentTest` (androidTest) | Compose UI renders a character name; search bar shows when state says so |

Run them with:

```bash
./gradlew testDebugUnitTest        # JVM unit tests
./gradlew connectedDebugAndroidTest # instrumented Compose UI test (needs device/emulator)
```

## Building & running

**Recommended:** open the project root in **Android Studio (Koala/2024.1+)**
and click Run — Android Studio will generate the Gradle wrapper jar and sync
automatically.

**Command line:**

```bash
git clone <your-repo-url>
cd RickMortyDemo
# If gradle-wrapper.jar isn't present (see note below), generate it once:
gradle wrapper --gradle-version 8.9
./gradlew assembleDebug
./gradlew installDebug   # with a device/emulator attached
```

> **Note on the Gradle wrapper jar:** this project ships `gradlew`,
> `gradlew.bat` and `gradle-wrapper.properties`, but the small binary
> `gradle/wrapper/gradle-wrapper.jar` isn't included in this generated
> bundle (binary files don't transfer well through this channel, and the
> sandbox this was built in has no network access to `services.gradle.org`
> to fetch one). Opening the project in Android Studio regenerates it
> automatically, or run `gradle wrapper` once locally if you have Gradle
> installed. This is the only manual step needed before `./gradlew` works.

No API key or `local.properties` secrets are required — the Rick & Morty API
is free and public.

## CI: GitHub Actions

`.github/workflows/android.yml` runs on every push/PR to `main`:

1. **lint-and-unit-test** — `./gradlew lintDebug` + `./gradlew testDebugUnitTest`, uploads reports as artifacts
2. **fastlane** — runs the `ci` Fastlane lane (`lint` → `test` → `build_debug`) via `bundle exec fastlane android ci`
3. **build** — `./gradlew assembleDebug`, uploads the APK as a build artifact
4. **instrumented-test** — runs the Compose UI test on a macOS-hosted emulator (`reactivecircus/android-emulator-runner`)

## Fastlane

```bash
bundle install                  # installs fastlane from the Gemfile
bundle exec fastlane android lint
bundle exec fastlane android test
bundle exec fastlane android build_debug
bundle exec fastlane android ci              # lint + test + build_debug, what CI runs
bundle exec fastlane android build_bundle     # release .aab
bundle exec fastlane android deploy_internal  # upload to Play Store internal track (needs a service-account json + signing config)
```

`fastlane/Appfile` sets the package name (`com.demo.rickmorty`);
`deploy_internal` is left ready-to-wire for a real Play Store upload (add a
service account JSON path and release signing config to use it for real).

## What I'd add next (good talking points for an interview)

- A detail screen (nav-compose is already a dependency) reached via the
  `NavigateToDetail` effect that's already wired but currently just shows a
  Toast
- A `RemoteMediator` + Room cache for true offline-first paging
- Screenshot/snapshot tests (e.g. Paparazzi) for the Compose UI
- A `BuildConfig`-driven base URL / flavors for staging vs. prod
