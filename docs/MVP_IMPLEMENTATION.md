# GrateLock MVP — Incremental Implementation Plan

## Context

GrateLock is a greenfield Android app that blocks distracting apps until the user photographs their handwritten gratitude journal entry or types 3 things they're grateful for. The project currently has zero code — only documentation (`CLAUDE.md` and `docs/MVP_SPEC.md`). This plan breaks the full MVP into 11 self-contained, testable increments that build on each other, prioritizing the core blocking mechanic early and layering features on top.

**Base package**: `com.gratelock.app`
**Source root**: `app/src/main/java/com/gratelock/app/`
**Resource root**: `app/src/main/res/`
**Test root**: `app/src/test/java/com/gratelock/app/`

---

## Increment 1: Project Scaffolding

**Goal**: Stand up a buildable, runnable Android project with Hilt DI, Compose theming, and a placeholder Home screen.

**Scope**:

- `build.gradle.kts` (root) — AGP, Kotlin, Hilt, KSP/kapt plugins
- `settings.gradle.kts` — module include, repository config
- `gradle.properties` — AndroidX, Compose compiler flags
- `app/build.gradle.kts` — all dependencies (spec §13), SDK config (§14.2)
- `app/src/main/AndroidManifest.xml` — Application + MainActivity + all permissions declared
- `GrateLockApplication.kt` — `@HiltAndroidApp`
- `MainActivity.kt` — `@AndroidEntryPoint` ComponentActivity hosting NavHost
- `ui/theme/Color.kt` — warm palette (cream #FFF8F0, beige #F5E6D3, sage #A8B5A0, dusty rose #C9A9A6)
- `ui/theme/Type.kt` — Nunito/Poppins headers, system sans-serif body
- `ui/theme/Theme.kt` — `GrateLockTheme` with Material 3 custom color scheme
- `ui/navigation/NavGraph.kt` — `sealed class Screen` with all routes, NavHost with Home start
- `ui/navigation/BottomNavBar.kt` — bottom nav (Home, Jar, Wrap, Settings)
- `ui/screen/home/HomeScreen.kt` + `HomeViewModel.kt` — placeholder with bottom nav
- `di/AppModule.kt` — empty Hilt module
- `util/Constants.kt` — prefs name, channel IDs, validation thresholds
- Resources: `strings.xml`, `colors.xml`, `themes.xml`, Nunito font files

**Testable State**: `./gradlew assembleDebug` succeeds. App launches with warm theme and working bottom nav tabs.

**Deliverable**: Running app shell with the correct visual identity — cream backgrounds, sage accents, Nunito headers.

---

## Increment 2: Data Layer (Room + Repositories)

**Goal**: Build the entire persistence layer so all subsequent increments have data infrastructure ready.

**Scope**:

- `data/local/entity/JournalEntry.kt` — all fields from spec §6.1
- `data/local/entity/BlockedApp.kt` — packageName PK, appName, isBlocked, addedAt
- `data/local/entity/UserStats.kt` — singleton row with streak, unlock, onboarding fields
- `data/local/entity/WeeklyWrap.kt` — weekly stats + image path
- `data/local/dao/EntryDao.kt` — insert, getByDate, getAll, getPhotoHashesRecent
- `data/local/dao/BlockedAppDao.kt` — insert, getBlocked, getBlockedPackageNames
- `data/local/dao/StatsDao.kt` — getStats, updateStreak, updateUnlockStatus
- `data/local/dao/WeeklyWrapDao.kt` — insert, getAll, getLatest
- `data/local/AppDatabase.kt` — Room DB with all 4 entities
- `data/repository/EntryRepository.kt` — interface + impl
- `data/repository/BlockedAppRepository.kt` — interface + impl
- `data/repository/StatsRepository.kt` — interface + impl
- `data/repository/WeeklyWrapRepository.kt` — interface + impl
- `data/local/GrateLockPreferences.kt` — SharedPreferences wrapper (fast cache for blocking service)
- `di/RepositoryModule.kt` — Hilt bindings
- `util/DateUtils.kt` — todayDateString(), yesterdayDateString(), etc.
- Tests: DAO instrumented tests, DateUtils unit tests

**Testable State**: All DAO tests pass. Repositories inject into ViewModels. App still launches.

**Deliverable**: Fully operational data layer — every entity, DAO, and repository the app needs.

---

## Increment 3: App Selection Screen (F1)

**Goal**: Users choose which installed apps to block.

**Scope**:

- `domain/model/InstalledApp.kt` — packageName, appName, icon, isBlocked, category
- `domain/usecase/GetInstalledAppsUseCase.kt` — query PackageManager, categorize, merge with BlockedApp state
- `ui/screen/appselection/AppSelectionScreen.kt` — LazyColumn with search, category grouping, toggles, "Select All Social Media" button, save
- `ui/screen/appselection/AppSelectionViewModel.kt` — state + events, saves to BlockedAppRepository + syncs to prefs
- `ui/component/AppListItem.kt` — reusable list item with icon, name, category, toggle
- NavGraph update: add AppSelection route
- Tests: categorization logic, ViewModel toggle/save/search

**Testable State**: Browse installed apps with icons, search/filter, toggle blocking, save persists to Room.

**Deliverable**: Polished App Selection screen. Selections persist across restarts.

---

## Increment 4: Core Blocking Mechanism (F2 + F5 partial)

**Goal**: Opening a blocked app triggers the full-screen overlay. This is the core product mechanic.

**Scope**:

- `res/xml/accessibility_service_config.xml` — event types, feedback type config
- `service/BlockingAccessibilityService.kt` — listens for TYPE_WINDOW_STATE_CHANGED, checks blocked packages from prefs, launches overlay with 500ms debounce
- `ui/screen/overlay/BlockingOverlayActivity.kt` — undismissable full-screen activity (excludeFromRecents, empty taskAffinity), shows blocked app icon/name, two CTAs ("Type it" / "Photograph my journal"), streak display, emergency unlock link. Temporary: both CTAs just unlock for the day.
- `ui/screen/overlay/BlockingOverlayScreen.kt` — Compose UI per spec S2
- `ui/screen/overlay/BlockingOverlayViewModel.kt` — loads app info, streak, handles events
- `domain/usecase/EmergencyUnlockUseCase.kt` — check remaining, decrement, unlock, no streak effect
- `service/DailyResetWorker.kt` — midnight: isUnlockedToday = false in Room + prefs
- `service/WeeklyResetWorker.kt` — Monday midnight: reset emergency unlock counter
- `service/BootReceiver.kt` — re-schedule workers on BOOT_COMPLETED
- `util/PermissionUtils.kt` — check/open accessibility, overlay, usage stats permissions
- AndroidManifest updates: AccessibilityService, BootReceiver, BlockingOverlayActivity declarations
- Tests: EmergencyUnlockUseCase, overlay ViewModel

**Testable State**: Enable AccessibilityService, select a blocked app, open it -> overlay appears. Can't dismiss via back. Emergency unlock works. Daily reset re-engages blocking.

**Deliverable**: Core product mechanic works end-to-end.

---

## Increment 5: Typed Gratitude Entry (F3a + F4 partial)

**Goal**: Complete the "Type 3 things" entry path with full validation.

**Scope**:

- `util/TextValidator.kt` — gibberish detection (repeated chars, keyboard patterns, sequential chars, word repetition), duplicate detection (Levenshtein), minimum content check
- `domain/usecase/ValidateTypedEntryUseCase.kt` — per-field validation with warm error messages
- `domain/model/ValidationResult.kt` — Valid / Invalid with per-field errors
- `ui/screen/gratitudeform/GratitudeFormScreen.kt` — 3 OutlinedTextFields, placeholder rotation, character indicators, submit button, inline errors
- `ui/screen/gratitudeform/GratitudeFormViewModel.kt` — field updates, validation, save entry, update streak, unlock
- `domain/usecase/ExtractKeywordsUseCase.kt` — tokenize, remove stop words, top 5
- `domain/usecase/UnlockAppsUseCase.kt` — set isUnlockedToday in Room + prefs
- `domain/usecase/CalculateStreakUseCase.kt` — increment/reset logic, longestStreak update
- Overlay update: "Type it" CTA navigates to GratitudeFormScreen
- Tests: TextValidator (comprehensive), ValidateTypedEntry, CalculateStreak, ExtractKeywords, FormViewModel

**Testable State**: Blocked app -> overlay -> "Type it" -> form -> valid entries -> unlock. Gibberish rejected with warm messages. Entries persist in Room.

**Deliverable**: First complete unlock flow works end-to-end.

---

## Increment 6: Camera & Photo Entry (F3b + F4 complete)

**Goal**: CameraX photo capture + ML Kit validation, completing the second entry path.

**Scope**:

- `ui/screen/camera/CameraScreen.kt` — CameraX Preview, guide overlay, capture button, flash toggle
- `ui/screen/camera/CameraViewModel.kt` — state: Previewing/Capturing/Processing/Error
- `util/PhotoValidator.kt` — ML Kit text recognition: >=3 text blocks, >=30 chars, >=0.5 confidence
- `util/PerceptualHash.kt` — pHash: 32x32 grayscale -> average hash, hamming distance for duplicate detection
- `domain/usecase/ValidatePhotoEntryUseCase.kt` — text detection + duplicate check (last 30 hashes, 95% threshold)
- `ui/screen/validation/ValidationScreen.kt` + `ValidationViewModel.kt` — processing animation, success/error transitions
- `ui/screen/success/SuccessScreen.kt` + `SuccessViewModel.kt` — "Apps unlocked", streak, "Go to [App]" button
- Overlay update: "Photograph my journal" CTA navigates to CameraScreen
- Tests: PerceptualHash, ValidatePhotoEntry, CameraViewModel

**Testable State**: Blocked app -> overlay -> "Photograph" -> camera -> capture -> ML Kit validates -> success or retry. Both entry paths fully functional.

**Deliverable**: Both unlock paths complete. Core product is feature-complete for the daily loop.

---

## Increment 7: Home Screen + Happiness Jar (F6, S1, S6)

**Goal**: Real Home screen with streak/jar visuals, and the Jar gallery of past entries.

**Scope**:

- `ui/component/HappinessJar.kt` — Canvas-drawn jar with animated fill level
- `ui/component/StreakBadge.kt` — streak count + fire icon
- `ui/component/TodayStatus.kt` — "Unlocked" / "Journal to unlock"
- `ui/component/GratitudeCard.kt` — typed entry display card (3 items, date)
- `ui/component/PhotoThumbnail.kt` — rounded image thumbnail via Coil
- `ui/screen/home/HomeScreen.kt` — full implementation: StreakBadge, HappinessJar, TodayStatus
- `ui/screen/home/HomeViewModel.kt` — observes stats, computes jar fill
- `ui/screen/jar/JarScreen.kt` — LazyVerticalGrid (3 cols), photo thumbnails + typed cards
- `ui/screen/jar/JarViewModel.kt` — observes all entries
- `ui/screen/jar/EntryDetailScreen.kt` — full-screen entry view
- NavGraph: wire Home + Jar to bottom nav, add EntryDetail route
- Tests: HomeViewModel, JarViewModel

**Testable State**: Home shows real streak, jar fill, unlock status. Jar tab shows all entries in grid. Tap for detail.

**Deliverable**: Users see progress, browse past entries. Emotional reward loop (jar filling, streak growing) is live.

---

## Increment 8: Streak Polish + Onboarding (F7 + first-time UX)

**Goal**: Harden streak edge cases, build onboarding flow, handle permissions gracefully.

**Scope**:

- `domain/usecase/CalculateStreakUseCase.kt` — update: retroactive break on midnight reset, timezone handling, multi-day gap
- `service/DailyResetWorker.kt` — update: check if yesterday had no entry -> reset streak to 0
- `ui/screen/onboarding/OnboardingScreen.kt` — 3-4 page horizontal pager: Welcome, How It Works, Permissions (with grant buttons + checkmarks), App Selection (inline)
- `ui/screen/onboarding/OnboardingViewModel.kt` — page nav, permission tracking, complete gating
- `ui/component/PermissionCard.kt` — reusable permission status card
- `domain/usecase/CheckStreakMilestoneUseCase.kt` — detect milestones (7, 14, 30, 60, 90, 100, 365)
- NavGraph: start at Onboarding if `!onboardingComplete`, then Home
- HomeViewModel update: check AccessibilityService on resume, show re-enable banner
- Tests: streak edge cases, onboarding ViewModel, milestone detection

**Testable State**: Fresh install -> onboarding -> permissions -> app selection -> Home. Streak edge cases all pass. Permission revocation shows banner.

**Deliverable**: Polished first-time UX. Streak tracking is robust. App feels complete for daily use.

---

## Increment 9: Settings Screen (S9)

**Goal**: User configuration — manage blocked apps, blocking toggle, data management.

**Scope**:

- `ui/screen/settings/SettingsScreen.kt` — grouped list: App Blocking, Schedule, Notifications, Emergency Unlock info, Data, About
- `ui/screen/settings/SettingsViewModel.kt` — toggle blocking, reset streak, delete all data
- `ui/component/ConfirmationDialog.kt` — reusable warm-styled dialog
- `domain/usecase/DeleteAllDataUseCase.kt` — clear all Room tables, reset prefs, delete photos
- GrateLockPreferences update: add reminder/notification prefs
- NavGraph: wire Settings to bottom nav
- Tests: SettingsViewModel, DeleteAllData

**Testable State**: Toggle blocking on/off. Reset streak with confirmation. Delete all data resets to onboarding.

**Deliverable**: Users have full control. All four bottom nav tabs have real content.

---

## Increment 10: Weekly Wrap Generation & Sharing (F8)

**Goal**: Auto-generate shareable weekly wrap cards with stats.

**Scope**:

- `domain/usecase/GenerateWrapUseCase.kt` — compute weekly stats, create WeeklyWrap entity
- `domain/usecase/GenerateWrapImageUseCase.kt` — render 1080x1920 PNG via Canvas API (spec §8.1 layout)
- `util/CanvasExtensions.kt` — helper extensions for Canvas rendering
- `service/WeeklyWrapWorker.kt` — Sunday 8 PM, runs GenerateWrapUseCase, sends notification
- `ui/screen/wrap/WrapScreen.kt` — latest wrap preview, share button, previous wraps list
- `ui/screen/wrap/WrapViewModel.kt` — observe wraps, share events
- `ui/component/WrapCard.kt` — wrap summary card
- `util/ShareUtils.kt` — FileProvider URI, ACTION_SEND with image/png
- `res/xml/file_paths.xml` — FileProvider config
- UserStats update: add `weeklyBlockedAttempts` counter
- BlockingAccessibilityService update: increment blocked attempts counter
- NavGraph: wire Wrap tab
- Tests: GenerateWrapUseCase stats computation, WrapViewModel

**Testable State**: Trigger wrap generation -> appears in Wrap tab -> Share opens Android share sheet with correct PNG.

**Deliverable**: Viral growth mechanic in place. Beautiful shareable cards.

---

## Increment 11: Notifications + Polish

**Goal**: All notification types, analytics logging, animations, edge case handling.

**Scope**:

- `service/NotificationHelper.kt` — 4 channels: reminders, streaks, wraps, celebrations
- `service/ReminderWorker.kt` — configurable daily reminder if not journaled
- `service/StreakAtRiskWorker.kt` — 9 PM alert if streak > 0 and no entry today
- `data/local/entity/AnalyticsEvent.kt` + `dao/AnalyticsDao.kt` + `data/repository/AnalyticsRepository.kt` — local event logging (spec §10)
- `domain/usecase/LogAnalyticsEventUseCase.kt` — convenience wrapper, sprinkle throughout existing code
- AppDatabase: bump version, add AnalyticsEvent entity + migration
- Animation polish: jar fill animation, streak fire pulse, success confetti, validation shimmer
- Edge case handling: already-journaled overlay auto-dismiss, midnight reset during active use, rapid app switching, keyboard handling in form
- Worker scheduling in GrateLockApplication.kt on startup
- Full regression testing

**Testable State**: Notifications fire correctly. Analytics accumulate in Room. Animations feel smooth. Edge cases handled.

**Deliverable**: MVP is complete, polished, and ready for user testing.

---

## Dependency Graph

```
Increment 1: Scaffolding
    |
Increment 2: Data Layer
    |
    +-- Increment 3: App Selection (F1)
    |       |
    +-------+-- Increment 4: Blocking Mechanism (F2)
                    |
                    +-- Increment 5: Typed Entry (F3a)
                    |       |
                    +-- Increment 6: Camera & Photo (F3b)
                    |       |
                    +-------+-- Increment 7: Home + Jar (F6)
                                    |
                                    +-- Increment 8: Streaks + Onboarding (F7)
                                    |       |
                                    |       +-- Increment 9: Settings (S9)
                                    |
                                    +-- Increment 10: Weekly Wrap (F8)
                                    |
                                    +-- Increment 11: Notifications + Polish
```

Increments 9, 10, 11 can be partially parallelized after Increment 8.

---

## Key Architectural Decisions

1. **BlockingOverlayActivity is a separate Activity** — launched by AccessibilityService with `FLAG_ACTIVITY_NEW_TASK`, has its own mini NavHost for the entry flow
2. **SharedPreferences is a write-through cache, never source of truth** — AccessibilityService reads prefs for speed, but all writes go Room-first then sync to prefs
3. **500ms debounce on overlay launches** — prevents stacking from rapid app switching
4. **Streak breaks are retroactive** — streak stays visible during the day, only resets at midnight if previous day had no entry
5. **Photos stored in internal app storage** (`context.filesDir`) — no external storage permissions needed
6. **Wrap images rendered via Canvas API** — reliable, background-thread safe, produces clean 1080x1920 PNGs
7. **Analytics logged locally to Room** — simple eventName + JSON eventData, ready for backend pipe post-MVP

---

## Verification Strategy

After each increment:
1. `./gradlew assembleDebug` — build succeeds
2. `./gradlew testDebugUnitTest` — all unit tests pass
3. `./gradlew lint` — no new lint errors
4. Manual testing on device/emulator for the increment's testable state
5. Regression check: all previous increment features still work
