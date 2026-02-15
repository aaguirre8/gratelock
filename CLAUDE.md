# Project: GrateLock Android App

## Platform Requirements
- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM with Repository pattern
- **DI**: Hilt
- **Database**: Room (SQLite)
- **Camera**: CameraX
- **On-Device ML**: ML Kit text recognition
- **Background Work**: WorkManager + Foreground Service
- **Image Loading**: Coil
- **Navigation**: Navigation Compose
- **Min SDK**: 26 (Android 8.0) / **Target SDK**: 34 (Android 14)

## Branch Strategy
1. Create feature branches from `main`: `feature/[short-description]`
2. Bugfix branches: `fix/[short-description]`
3. Use conventional commits: `feat:`, `fix:`, `refactor:`, `test:`, `chore:`
4. Run `./gradlew lint` and `./gradlew testDebugUnitTest` before committing
5. Keep commits atomic — one logical change per commit

## Code Organization
- **Screens**: `ui/screen/[name]/[Name]Screen.kt`
- **ViewModels**: `ui/screen/[name]/[Name]ViewModel.kt`
- **Components**: `ui/component/[Name].kt`
- **Navigation**: `ui/navigation/NavGraph.kt` (routes as sealed class members, never hardcoded strings)
- **UseCases**: `domain/usecase/[Verb][Noun]UseCase.kt`
- **Repositories**: `data/repository/[Name]Repository.kt`
- **DAOs**: `data/local/dao/[Name]Dao.kt`
- **Entities**: `data/local/entity/[Name].kt`
- **Services**: `service/[Name]Service.kt`
- **Workers**: `service/[Name]Worker.kt`
- **Resources**: `snake_case` — `ic_jar_filled`, `bg_overlay_blur`, `str_unlock_success`

## Architecture Standards
- Screens observe ViewModel state via `StateFlow`. No business logic in Composables.
- ViewModels never access DAOs directly — always through a Repository.
- Complex logic (validation, streak calculation, wrap generation) lives in `domain/usecase/`.
- ViewModels expose a single `StateFlow<[Name]UiState>` sealed interface and a `fun onEvent([Name]Event)` handler.
- Define UI state as a `sealed interface` with `Loading`, `Success`, and `Error` variants.
- Room is the single source of truth. SharedPreferences is only a fast-access cache for the blocking service — always sync from Room, never the reverse.
- Annotate ViewModels with `@HiltViewModel`. Provide Repositories and UseCases via Hilt `@Module`.

## Adding a New Screen
Follow `ui/screen/home/` as the canonical reference:
1. Create package `ui/screen/[name]/`
2. `[Name]Screen.kt` — receives state and emits events. No ViewModel access in child composables.
3. `[Name]ViewModel.kt` — `@HiltViewModel` with `StateFlow` + `onEvent` pattern.
4. Add route to `NavGraph.kt`
5. Write `@Preview` composables for each state variant

## Design Guidelines
- Warm, minimal aesthetic — cream/beige neutrals with sage green or dusty rose accent. No cold blues/grays.
- Rounded sans-serif headers (Nunito or Poppins). Clean sans-serif body.
- Outlined, soft, rounded icons. No sharp/angular styles.
- Subtle animations only — jar filling, streak fire, card transitions.
- All user-facing copy must be warm and encouraging, never punitive or guilt-tripping.

## Testing Requirements
- Unit tests (JUnit) for all UseCases and ViewModels
- Instrumented tests with in-memory Room DB for DAOs
- `@Preview` composables for all reusable components
- Critical edge case coverage: timezone changes, midnight resets during active use, rapid service events, permission revocation
- Run `./gradlew testDebugUnitTest` before committing

## Build Commands
```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build signed release APK
./gradlew testDebugUnitTest      # Run unit tests
./gradlew lint                   # Run lint checks
./gradlew installDebug           # Install on connected device
```

## Coding Conventions
- KDoc on all public classes and functions (`@param`, `@return`)
- `data class` for entities and models, `sealed class/interface` for UI state and navigation routes
- Extension functions where they improve readability
- All analytics events logged locally in Room — no remote analytics
- Image storage uses internal app storage only — no external storage permissions
- Compose `@Preview` for every reusable component