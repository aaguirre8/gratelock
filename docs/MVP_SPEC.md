# GrateLock — MVP Product Requirements & Technical Specification

## Document Purpose

This document contains everything needed to build the MVP of GrateLock, an Android app that blocks distracting apps until the user photographs their handwritten gratitude journal entry. This spec is intended to be consumed by an AI coding agent (Claude Code) to scaffold and build the entire project.

---

## 1. Product Overview

### 1.1 One-Liner

GrateLock locks your distracting apps until you photograph today's handwritten gratitude journal entry.

### 1.2 Core Value Proposition

Users already want to journal and already feel guilty about screen time. GrateLock bridges both problems: it forces a gratitude journaling habit by using app access as the reward. The user must physically write in their journal and photograph it to unlock their apps for the day.

### 1.3 Target Audience

Women aged 18–35 in the wellness/self-care space who own bullet journals, follow gratitude accounts on social media, and feel guilty about their screen time. Secondary: men in the self-improvement/stoicism space.

### 1.4 Competitive Landscape

- **Prayer Lock** — blocks apps until user prays. $20K/month revenue. 73K+ users. Same mechanic but for Christians.
- **Gratitude journal apps** (365 Gratitude, Happyfeed, Three Good Things) — standalone journals, no app-blocking mechanic.
- **App blockers** (Forest, Opal, ScreenZen, WalkLock) — block apps but don't tie unlocking to journaling.
- **No app combines gratitude journaling (especially handwritten photo verification) with app blocking.** This intersection is empty.

### 1.5 Psychological Mechanisms Exploited

- **Temptation bundling**: pair something you should do (journal) with something you want (scroll Instagram).
- **Loss aversion / sunk cost**: the Happiness Jar and streaks create accumulated value users don't want to lose.
- **Commitment device**: user voluntarily locks themselves into the behavior.
- **Identity reinforcement**: "I'm someone who practices gratitude" — the app reinforces this identity daily.
- **Social proof via sharing**: weekly/monthly wraps designed to be shared on Instagram Stories, creating organic viral growth.

---

## 2. MVP Feature Set

### 2.1 Core Features (Must Have for MVP)

| # | Feature | Description |
|---|---------|-------------|
| F1 | **App Selection** | User selects which apps to block (e.g., Instagram, TikTok, YouTube, Twitter, Reddit) |
| F2 | **Blocking Overlay** | When user opens a blocked app, a full-screen overlay appears requiring gratitude entry |
| F3a | **Gratitude Entry: Quick Type** | User types 3 things they're grateful for in a simple form (3 text fields, minimum 10 characters each). This is the fast/default path. |
| F3b | **Gratitude Entry: Journal Photo** | User takes a live photo (camera only, no gallery upload) of their handwritten bullet journal. This is the "premium" path that reinforces the offline habit. |
| F4 | **Basic Validation** | For typed entries: minimum character count per field (10 chars) + basic gibberish detection (reject "asdfasdf", repeated characters, keyboard mashing). For photo entries: verify the photo contains handwritten text (not a blank page, not a screenshot, not a previously used photo). |
| F5 | **Daily Unlock** | Once a valid entry (typed OR photo) is submitted, all blocked apps are unlocked for the rest of the day |
| F6 | **Happiness Jar** | Gallery of all submitted journal photos, displayed as a visual "jar" that fills over time |
| F7 | **Streak Tracking** | Track consecutive days of journaling. Display current streak and longest streak |
| F8 | **Weekly Wrap** | Auto-generated shareable card with weekly stats (days journaled, streak, apps blocked) |

### 2.2 Nice-to-Have (Post-MVP but design for them)

| # | Feature | Description |
|---|---------|-------------|
| N1 | **OCR + Word Cloud** | Extract text from handwritten photos, generate word clouds of gratitude themes. Note: typed entries already provide text data — word clouds can be generated from typed entries in MVP, photos require OCR post-MVP. |
| N2 | **Monthly/Yearly Wrapped** | Spotify-style full wrapped experience with deep stats and beautiful shareable cards |
| N3 | **Sentiment Analysis** | Track emotional tone of entries over time. "Your gratitude mood has been rising this month" |
| N4 | **Shake the Jar** | Shake phone to display a random past entry from the Happiness Jar (for bad days) |
| N5 | **Subscription / Premium** | Unlock OCR insights, advanced stats, no data sharing |
| N6 | **Anonymous Data Pipeline** | Aggregate anonymized gratitude data for research/B2B insights (free tier monetization) |

---

## 3. User Flows

### 3.1 Onboarding Flow (First Launch)

```
[Welcome Screen]
  "Gratitude first, scroll second"
  Brief explanation + benefits
  ↓
[Permission Requests]
  1. Usage Access permission (required for app monitoring)
  2. Overlay permission (required for blocking screen)
  3. Camera permission (required for photo capture)
  4. Notification permission (optional, for reminders)
  ↓
[App Selection Screen]
  Show list of installed apps (filtered to social/entertainment)
  User toggles which apps to block
  Minimum 1 app must be selected
  ↓
[Confirmation]
  "Starting tomorrow, [App1], [App2]... will be locked until you journal"
  "Type 3 things you're grateful for, or photograph your bullet journal to unlock"
  ↓
[Home Screen]
  App is ready. Blocking begins.
```

### 3.2 Daily Unlock Flow (Core Loop)

```
[User opens Instagram (blocked)]
  ↓
[GrateLock Overlay appears - full screen]
  Header: "Before you scroll..."
  Subheader: "What are you grateful for today?"
  Visual: Jar icon showing fill progress
  Current streak: "🔥 12 days"
  Two CTAs:
    [✏️ Type it] (primary - quick path)
    [📸 Photograph my journal] (secondary - deeper path)
  ↓

─── PATH A: Quick Type ───

[User taps "Type it"]
  ↓
[Gratitude Form Screen]
  3 text input fields, each with placeholder text:
    1. "I'm grateful for..." (placeholder: "e.g., my morning coffee")
    2. "I'm grateful for..." (placeholder: "e.g., a good conversation with Mom")
    3. "I'm grateful for..." (placeholder: "e.g., the sunshine today")
  Each field: minimum 10 characters, single line, max 200 characters
  Submit button (disabled until all 3 fields meet minimum)
  Back link to return to method selection
  ↓
[Validation runs]
  Check: each field ≥ 10 characters
  Check: not gibberish (reject repeated chars like "aaaaaaaaaa", keyboard patterns like "asdfasdfas", or identical entries across fields)
  ↓
  IF VALID → [Success Screen] (see below)
  IF INVALID → Inline field errors: "Write a bit more — what are you truly grateful for? ✨"

─── PATH B: Journal Photo ───

[User taps "Photograph my journal"]
  ↓
[In-app camera opens (NOT system camera, to prevent gallery access)]
  ↓
[User takes photo of journal page]
  ↓
[Validation runs]
  Quick check: is this a photo of handwritten text?
  Quick check: is this photo unique (not a duplicate of a previous submission)?
  ↓
  IF VALID → [Success Screen] (see below)
  IF INVALID:
    [Rejection Screen]
    "Hmm, that doesn't look like a journal entry. Try again?"
    Reason displayed (e.g., "No handwriting detected", "This looks like a previous entry")
    ↓
    [Back to camera]

─── SHARED: Success Screen ───

[Success Screen]
  "Beautiful! Your apps are unlocked for today ✨"
  "Day 13 — keep it going!"
  Entry is saved to Happiness Jar (typed entries get a generated card, photos saved as-is)
  Streak is updated
  All blocked apps are now accessible until midnight
  ↓
  [Redirect to originally requested app]
```

### 3.3 Happiness Jar Flow

```
[User opens GrateLock app → Jar tab]
  ↓
[Visual jar/grid of all submitted entries]
  Sorted by date (newest first)
  Jar fill animation based on total entries
  Two visual styles for entries:
    - Photo entries: photo thumbnail
    - Typed entries: generated card with the 3 gratitude items in a nice typographic layout
  Each entry shows: thumbnail/card, date, streak day number, entry type icon (📸 or ✏️)
  ↓
[Tap on entry]
  Photo entries: full-screen photo view with date overlay
  Typed entries: full-screen card with the 3 items displayed beautifully
  (Post-MVP: OCR extracted text from photos, sentiment)
```

### 3.4 Weekly Wrap Flow

```
[Every Sunday evening, notification:]
  "Your weekly gratitude wrap is ready ✨"
  ↓
[User opens wrap]
  Shareable card showing:
    - Days journaled this week (e.g., "6/7 days")
    - Current streak
    - Total jar entries
    - "You chose gratitude over scrolling X times"
    - (Post-MVP: top gratitude words from OCR)
  ↓
[Share button]
  Opens Android share sheet (Instagram Stories, WhatsApp, etc.)
  Card is pre-rendered as an image optimized for Instagram Stories (1080x1920)
```

### 3.5 Settings Flow

```
[Settings Screen]
  - Blocked Apps: edit which apps are blocked
  - Unlock Time: when do apps re-lock? (default: midnight)
  - Reminders: toggle and set time for "time to journal" notification
  - Emergency Unlock: allow 1 free unlock per week without photo (with confirmation dialog and guilt message)
  - Reset Streak: manual reset option
  - Data & Privacy: view/export/delete data
  - About / Support
```

---

## 4. Screen-by-Screen UI Specification

### 4.1 Design Language

- **Style**: Clean, minimal, warm. Think "calm wellness app" not "tech productivity tool."
- **Color Palette**: Warm neutrals (cream, soft beige) with an accent color (sage green or dusty rose). Avoid cold blues and grays.
- **Typography**: Rounded sans-serif for headers (e.g., Nunito, Poppins). Clean sans-serif for body.
- **Iconography**: Outlined, soft, rounded icons. No sharp/angular styles.
- **Animations**: Subtle, gentle. Jar filling, streak fire flickering, card transitions.
- **Tone of voice**: Warm, encouraging, slightly playful. Never punitive or guilt-tripping. Examples:
  - Good: "Before you scroll, take a moment for gratitude ✨"
  - Bad: "You haven't journaled yet! Apps are LOCKED."

### 4.2 Screens

#### S1: Home Screen
- Top: Current streak with fire emoji + "longest streak: X" subtitle
- Center: Happiness Jar visual (a jar illustration that fills based on total entries)
- Below jar: "X entries" count
- Bottom: Today's status — "Unlocked ✅" or "Journal to unlock 📝"
- Bottom nav: Home | Jar | Wrap | Settings

#### S2: Blocking Overlay (CRITICAL - this is the most seen screen)
- Full-screen, cannot be dismissed by back button
- Semi-transparent or blurred background
- Center content:
  - App icon of the blocked app at top
  - "Before you open [AppName]..."
  - "What are you grateful for today?"
  - Two CTA buttons stacked:
    - [✏️ Type 3 things] (primary button, large, filled)
    - [📸 Photograph my journal] (secondary button, outlined)
  - Current streak displayed subtly
  - Small text link: "Emergency unlock (X remaining this week)"

#### S2b: Gratitude Form Screen (Quick Type path)
- Clean, focused screen with warm background
- Header: "3 things you're grateful for right now"
- 3 text input fields with rounded borders, generous padding
- Placeholder text in each field (rotating examples like "my morning coffee", "a kind word from a friend", "being healthy")
- Character counter or subtle indicator when minimum is met (field border turns green/accent color)
- Submit button at bottom (disabled/grayed until all 3 fields valid)
- Back arrow to return to overlay method selection

#### S3: Camera Screen
- Full-screen camera viewfinder
- Overlay guide: subtle rounded rectangle showing where to position journal page
- Capture button at bottom center
- Flash toggle top-right
- Back/cancel top-left
- Text hint: "Position your journal entry in the frame"

#### S4: Validation / Processing Screen
- Brief loading state while validation runs
- Cute animation (e.g., sparkles, jar dropping in)
- Transitions to success or failure

#### S5: Success Screen
- Celebratory but not obnoxious
- "Your apps are unlocked ✨"
- "Day [X] of your gratitude journey"
- Streak counter updated
- "Go to [AppName]" button
- "View in Jar" secondary link

#### S6: Happiness Jar Screen
- Grid layout of photo thumbnails (3 columns)
- Visual jar at top showing fill level
- Total entries count
- Tap thumbnail → full screen view with date overlay
- (Post-MVP: shake to randomize / show random entry)

#### S7: Weekly Wrap Screen
- Card-style layout optimized for screenshot/sharing
- Stats displayed with large numbers and short labels
- Share button prominently placed
- Swipeable if there are multiple cards (e.g., stats card + encouragement card)
- Previous wraps viewable in a list

#### S8: App Selection Screen
- List of installed apps with icons and names
- Pre-filtered to show likely distracting apps first (social media, video, games)
- Search/filter bar at top
- Toggle switch per app
- "Select All Social Media" quick button
- Save button at bottom

#### S9: Settings Screen
- Standard list layout
- Grouped sections: App Blocking, Schedule, Notifications, Account, Data

---

## 5. Technical Architecture

### 5.1 Technology Stack

| Component | Technology |
|-----------|-----------|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Architecture | MVVM with Repository pattern |
| Local Database | Room (SQLite) |
| Image Storage | Local app storage (internal) |
| Camera | CameraX |
| DI | Hilt |
| Background Service | Foreground Service |
| App Monitoring | UsageStatsManager + AccessibilityService |
| Image Validation | On-device ML Kit (text recognition) or API call |
| Notifications | WorkManager |
| Share | Android Share Sheet + Bitmap/Canvas for card generation |

### 5.2 Project Structure

This is a suggested structure. Adjust it as you see fit.
```
com.gratelock.app/
├── di/                          # Hilt dependency injection modules
│   └── AppModule.kt
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt       # Room database
│   │   ├── dao/
│   │   │   ├── EntryDao.kt      # Journal entry DAO
│   │   │   ├── BlockedAppDao.kt # Blocked apps DAO
│   │   │   └── StatsDao.kt     # Stats/streaks DAO
│   │   └── entity/
│   │       ├── JournalEntry.kt
│   │       ├── BlockedApp.kt
│   │       └── UserStats.kt
│   └── repository/
│       ├── EntryRepository.kt
│       ├── BlockedAppRepository.kt
│       └── StatsRepository.kt
├── domain/
│   ├── model/                   # Domain models
│   ├── usecase/
│   │   ├── ValidateTypedEntryUseCase.kt
│   │   ├── ValidatePhotoEntryUseCase.kt
│   │   ├── UnlockAppsUseCase.kt
│   │   ├── CalculateStreakUseCase.kt
│   │   └── GenerateWrapUseCase.kt
├── service/
│   ├── AppMonitorService.kt     # Foreground service monitoring app usage
│   ├── BlockingAccessibilityService.kt  # Accessibility service for overlay
│   └── DailyResetWorker.kt     # WorkManager job to re-lock at midnight
├── ui/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   └── Theme.kt
│   ├── navigation/
│   │   └── NavGraph.kt
│   ├── screen/
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   ├── onboarding/
│   │   │   ├── OnboardingScreen.kt
│   │   │   └── OnboardingViewModel.kt
│   │   ├── overlay/
│   │   │   ├── BlockingOverlayActivity.kt  # Separate activity for overlay
│   │   │   └── BlockingOverlayViewModel.kt
│   │   ├── gratitudeform/
│   │   │   ├── GratitudeFormScreen.kt      # Quick type 3 things form
│   │   │   └── GratitudeFormViewModel.kt
│   │   ├── camera/
│   │   │   ├── CameraScreen.kt
│   │   │   └── CameraViewModel.kt
│   │   ├── jar/
│   │   │   ├── JarScreen.kt
│   │   │   └── JarViewModel.kt
│   │   ├── wrap/
│   │   │   ├── WrapScreen.kt
│   │   │   └── WrapViewModel.kt
│   │   ├── appselection/
│   │   │   ├── AppSelectionScreen.kt
│   │   │   └── AppSelectionViewModel.kt
│   │   └── settings/
│   │       ├── SettingsScreen.kt
│   │       └── SettingsViewModel.kt
│   └── component/               # Reusable composables
│       ├── HappinessJar.kt      # Jar visual component
│       ├── StreakBadge.kt
│       ├── WrapCard.kt
│       └── PhotoThumbnail.kt
└── util/
    ├── PhotoValidator.kt        # Image validation logic
    ├── TextValidator.kt         # Typed entry gibberish/duplicate detection
    ├── PerlinHash.kt            # Perceptual hash for duplicate detection
    └── Constants.kt
```

### 5.3 Android Permissions Required

```xml
<!-- AndroidManifest.xml -->

<!-- Required: monitor which app is in foreground -->
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"
    tools:ignore="ProtectedPermissions" />

<!-- Required: draw overlay on top of other apps -->
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

<!-- Required: camera for photo capture -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- Required: keep monitoring service alive -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />

<!-- Required: re-lock apps at midnight -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />

<!-- Optional: notifications for reminders and wraps -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Accessibility service declaration for blocking -->
<service
    android:name=".service.BlockingAccessibilityService"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
    android:exported="false">
    <intent-filter>
        <action android:name="android.accessibilityservice.AccessibilityService" />
    </intent-filter>
    <meta-data
        android:name="android.accessibilityservice"
        android:resource="@xml/accessibility_service_config" />
</service>
```

### 5.4 Core App Blocking Mechanism

This is the most critical technical component. The system works as follows:

**Approach: Accessibility Service + Overlay Activity**

1. **AccessibilityService** listens for `TYPE_WINDOW_STATE_CHANGED` events.
2. When a new window comes to foreground, the service checks the package name against the blocked apps list.
3. If the app is blocked AND the user hasn't unlocked today, the service launches `BlockingOverlayActivity` with `FLAG_ACTIVITY_NEW_TASK`.
4. `BlockingOverlayActivity` is a full-screen activity that covers the blocked app.
5. The overlay activity cannot be dismissed via back button (override `onBackPressed`).
6. Once the user completes the photo flow, the activity finishes and the originally blocked app is now visible.

**Important implementation details:**

- The service must maintain a `Set<String>` of blocked package names in memory for fast lookup.
- The service must track the "unlocked today" boolean (stored in SharedPreferences for speed, backed by Room).
- The daily reset (midnight) is handled by a `WorkManager` `PeriodicWorkRequest` that flips "unlocked today" back to false.
- The `BlockingOverlayActivity` must use `android:excludeFromRecents="true"` and `android:taskAffinity=""` to avoid appearing in the recent apps list.
- Handle edge case: user rapidly switches apps — debounce overlay launches (e.g., 500ms cooldown).

**Alternative approach (simpler but less reliable): UsageStatsManager polling**

A foreground service polls `UsageStatsManager` every 1-2 seconds to check the current foreground app. Less responsive but doesn't require Accessibility Service permission (which some users find scary). Consider this as a fallback if the Accessibility Service approach causes issues.

### 5.5 Entry Validation Logic

Validation differs by entry type. Both must prevent cheating while not being so strict that legitimate entries get rejected.

#### 5.5.1 Typed Entry Validation

**Validation checks (in order):**

1. **Minimum length**: Each of the 3 fields must contain at least 10 characters.
2. **No duplicates across fields**: All 3 entries must be meaningfully different from each other. Reject if any two fields have >80% string similarity (Levenshtein distance or simple normalized comparison).
3. **Gibberish detection**: Reject entries that match common gaming patterns:
   - Repeated characters: "aaaaaaaaaa", "1111111111"
   - Keyboard mashing: "asdfasdfas", "qwertyuiop", "zxcvbnm"
   - Sequential characters: "abcdefghij", "1234567890"
   - All-same-word repetition: "food food food food"
   - Implementation: a list of regex patterns + a check that the entry contains at least 2 distinct words of 3+ characters each.
4. **No repeat from yesterday**: Compare today's entries against yesterday's. If all 3 are identical to yesterday's 3, reject with: "Today is a new day — what's something fresh you're grateful for? 🌱"

**Validation error messages (inline, per field):**

- Too short → "Just a bit more — what specifically are you grateful for?"
- Gibberish detected → "Let's make this real — write something meaningful ✨"
- Duplicate fields → "Each one should be different — you have plenty to be grateful for!"
- Same as yesterday → "You wrote these yesterday! What's new today? 🌱"

**Tolerance policy for typed entries:** Be stricter than photo validation. Typing is fast and easy to game, so the validation needs to ensure minimum effort. However, don't over-engineer it — a user who writes "my cat Mochi being cute this morning" is being genuine even if it's simple.

#### 5.5.2 Photo Entry Validation

**Validation checks (in order):**

1. **Liveness check**: Photo must be taken with the in-app camera. No gallery access. The photo's EXIF timestamp must be within the last 60 seconds.
2. **Content check**: Use ML Kit's on-device text recognition (`com.google.mlkit:text-recognition`) to verify the image contains handwritten text. The image must have at least 3 detected text blocks (mapping to 3 gratitude items). Set a minimum confidence threshold (e.g., 0.5).
3. **Duplicate check**: Compute a perceptual hash (pHash) of the image. Compare against hashes of the last 30 entries. If similarity is above 95%, reject as duplicate. Store hashes in Room alongside each entry.
4. **Minimum content check**: The detected text should contain a minimum number of characters (e.g., 30 characters total across all blocks). This prevents photos of random objects with incidental text.

**Validation error messages (warm, not accusatory):**

- No text detected → "I can't see any writing — try holding your journal a bit closer 📖"
- Too little text → "Looks a bit short — make sure your full entry is in the frame ✨"
- Duplicate detected → "This looks familiar! Today is a new day — write a fresh entry 🌱"

**Tolerance policy for photo entries:** Err on the side of acceptance. A false acceptance (user cheats) is less damaging than a false rejection (user gets frustrated and uninstalls). The target is ~90% accuracy, not 100%. The primary barrier is the inconvenience of cheating, not the impossibility.

---

## 6. Data Models

**WARNING**: The data model is the starting baseline. Modify and/or build on top of that model as you see fit.

### 6.1 Room Entities

```kotlin
// Journal entry — one per day
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entryType: String,            // "typed" or "photo"
    val gratitude1: String? = null,   // typed entry: first gratitude item
    val gratitude2: String? = null,   // typed entry: second gratitude item
    val gratitude3: String? = null,   // typed entry: third gratitude item
    val photoPath: String? = null,    // photo entry: local file path to saved photo
    val photoHash: String? = null,    // photo entry: perceptual hash for duplicate detection
    val capturedAt: Long,             // timestamp of entry submission (epoch ms)
    val date: String,                 // "2026-02-14" — the calendar date this entry is for
    val ocrText: String? = null,      // photo entries: extracted text (null until processed, post-MVP)
    val sentimentScore: Float? = null, // -1.0 to 1.0 (null until processed, post-MVP)
    val keywords: String? = null,     // comma-separated keywords (from typed text or OCR)
    val isValid: Boolean = true       // validation result
)

// Blocked app entry
@Entity(tableName = "blocked_apps")
data class BlockedApp(
    @PrimaryKey val packageName: String,  // e.g., "com.instagram.android"
    val appName: String,                  // display name
    val isBlocked: Boolean = true,
    val addedAt: Long                     // when user added this app
)

// User stats / state
@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,          // singleton row
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalEntries: Int = 0,
    val lastEntryDate: String? = null,    // "2026-02-14"
    val isUnlockedToday: Boolean = false,
    val emergencyUnlocksRemaining: Int = 1, // resets weekly
    val onboardingComplete: Boolean = false
)

// Weekly wrap data (generated each Sunday)
@Entity(tableName = "weekly_wraps")
data class WeeklyWrap(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weekStartDate: String,            // "2026-02-10" (Monday)
    val weekEndDate: String,              // "2026-02-16" (Sunday)
    val daysJournaled: Int,
    val typedEntries: Int,                // how many days used quick type
    val photoEntries: Int,                // how many days used journal photo
    val streakAtEnd: Int,
    val totalEntriesAtEnd: Int,
    val blockedAttempts: Int,             // times the overlay was shown this week
    val topKeywords: String? = null,      // comma-separated (from typed text + OCR, post-MVP)
    val wrapImagePath: String? = null,    // path to generated shareable image
    val generatedAt: Long
)
```

### 6.2 SharedPreferences (fast access for the blocking service)

```
gratelock_prefs:
  is_unlocked_today: Boolean      # fast check for blocking service
  last_unlock_date: String        # "2026-02-14"
  blocking_enabled: Boolean       # global on/off switch
  blocked_packages: Set<String>   # cached set of blocked package names
  current_streak: Int             # cached for overlay display
  emergency_unlocks_remaining: Int
```

---

## 7. Business Logic Rules

### 7.1 Blocking Rules

- Blocking is active from the moment the user completes onboarding.
- A "day" runs from midnight to midnight in the user's local timezone.
- Once unlocked, all blocked apps remain accessible until the next midnight.
- If the user adds a new app to the blocked list after unlocking today, that app is also unlocked for the remainder of today.
- If blocking is globally disabled in settings, no overlay is shown. Streak continues to be tracked based on journal submissions only.

### 7.2 Streak Rules

- A streak increments when the user submits a valid entry on a new calendar day.
- A streak breaks if a full calendar day passes with no entry (e.g., if last entry was Monday and it's now Wednesday, the streak resets to 0).
- The streak does NOT break if today has no entry yet — it only breaks retroactively when the next day begins without an entry for the previous day.
- Longest streak is updated whenever current streak exceeds it.
- Emergency unlocks do NOT count as journal entries and do NOT extend the streak.

### 7.3 Emergency Unlock Rules

- Users get 1 emergency unlock per week (resets every Monday at midnight).
- Emergency unlock shows a confirmation dialog: "Are you sure? This won't count toward your streak, and you have [X] emergency unlocks left this week."
- Emergency unlocks are logged for analytics but do not create journal entries.

### 7.4 Weekly Wrap Generation Rules

- Wraps are generated every Sunday at 8:00 PM local time (via WorkManager).
- The wrap covers Monday through Sunday of the current week.
- A push notification is sent when the wrap is ready.
- Wraps are stored locally and remain accessible in the Wrap tab.
- The shareable image is rendered as a 1080x1920 PNG (Instagram Story format).
- **Keyword extraction for wraps**: For typed entries, keywords can be extracted directly from the gratitude text (simple word frequency, excluding stop words). This means basic word clouds are achievable in MVP without OCR. Photo-only entries contribute to word clouds only post-MVP when OCR is added.

---

## 8. Shareable Wrap Card Specification

### 8.1 Weekly Wrap Card Layout (1080x1920 px)

```
┌──────────────────────────────────────┐
│                                      │
│          [GrateLock logo]            │
│        My Week in Gratitude          │
│         Feb 10 – Feb 16             │
│                                      │
│  ┌──────────────────────────────┐    │
│  │                              │    │
│  │      6/7 days journaled      │    │
│  │      (4 typed, 2 photos)     │    │
│  │                              │    │
│  └──────────────────────────────┘    │
│                                      │
│         🔥 14-day streak             │
│                                      │
│    You chose gratitude over          │
│     scrolling 23 times               │
│                                      │
│  ┌──────┐ ┌──────┐ ┌──────┐         │
│  │ Mon ✓│ │ Tue ✓│ │ Wed ✓│         │
│  └──────┘ └──────┘ └──────┘         │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌────┐ │
│  │ Thu ✓│ │ Fri ✓│ │ Sat ✗│ │Sun✓│ │
│  └──────┘ └──────┘ └──────┘ └────┘ │
│                                      │
│     [Word cloud from typed entries │
│      + OCR for photos — N2]       │
│                                      │
│       Total jar entries: 47          │
│                                      │
│        gratelock.app 🫙             │
│                                      │
└──────────────────────────────────────┘
```

### 8.2 Card Generation

- Rendered using Android's `Canvas` API in a background thread.
- Saved as PNG to app's cache directory.
- Shared via `Intent.ACTION_SEND` with `image/png` MIME type using a `FileProvider` URI.
- Must include the app's branding/URL for organic acquisition.

---

## 9. Notification Strategy

| Notification | Timing | Content | Channel |
|---|---|---|---|
| Journal reminder | Configurable (default 8:00 AM) | "Good morning ☀️ Your apps are waiting — write 3 things you're grateful for" | `reminders` |
| Weekly wrap ready | Sunday 8:00 PM | "Your weekly gratitude wrap is ready ✨" | `wraps` |
| Streak at risk | 9:00 PM if no entry today | "Your 14-day streak is on the line — there's still time to journal 🔥" | `streaks` |
| Milestone celebration | On streak milestones (7, 14, 30, 60, 90, 100, 365) | "🎉 30 days of gratitude! You're unstoppable!" | `celebrations` |

---

## 10. Analytics Events to Track

Track these events locally in Room for now. Post-MVP, pipe to a backend.

```
app_opened                         # user opens GrateLock app
onboarding_started
onboarding_completed
app_selected_for_blocking          { package_name }
app_removed_from_blocking          { package_name }
overlay_shown                      { blocked_package, timestamp }
entry_method_selected              { method: typed|photo }
gratitude_form_opened
gratitude_form_submitted           { result: valid|invalid, reason }
camera_opened
photo_captured
photo_validated                    { result: valid|invalid, reason }
apps_unlocked                      { method: typed|photo|emergency }
emergency_unlock_used
jar_viewed
jar_entry_tapped                   { entry_id, entry_type }
wrap_viewed                        { week_start_date }
wrap_shared                        { week_start_date, share_target }
streak_broken                      { previous_streak }
streak_milestone                   { milestone: 7|14|30|... }
settings_changed                   { setting, old_value, new_value }
```

---

## 11. Edge Cases & Error Handling

| Scenario | Expected Behavior |
|---|---|
| User takes photo in very low light | ML Kit may fail to detect text. Show message: "It's a bit dark — try better lighting or use your flash 💡" |
| Phone has no camera | Extremely unlikely but: show error during onboarding, suggest using typed entry as fallback |
| User denies Usage Stats permission | App cannot function. Show explanation screen with button to open system settings. Do not allow proceeding without it. |
| User denies Overlay permission | Same as above — required permission. |
| User revokes Accessibility Service | Blocking stops working. Show persistent notification: "GrateLock can't protect your focus — tap to re-enable" |
| User force-stops GrateLock | Blocking stops. On next boot or app open, resume service. Register `BOOT_COMPLETED` receiver. |
| User clears app data | All entries lost. Treat as fresh install. (Post-MVP: cloud backup prevents this) |
| Midnight reset while user is actively using a blocked app | Do not interrupt active sessions. Lock takes effect on next app switch. |
| Two entries in one day | Only the first counts. If user opens camera or form again, show: "You already journaled today ✅ Enjoy your apps!" |
| User types in another language | Accept it. Don't validate language. Gibberish detection only catches patterns (keyboard mashing, repeated chars), not language-specific content. |
| User types very short but valid entries | Enforce 10-character minimum strictly. "My cat" = rejected. "My cat Mochi being sweet" = accepted. |
| User switches between typed and photo mid-flow | Allow it. Back button on form returns to method selection on overlay. No penalty. |
| User changes timezone (travel) | Use device's current timezone. A day is always midnight-to-midnight local. This may cause edge cases during travel — acceptable for MVP. |
| No internet connection | Everything works offline. Validation is on-device. No backend dependency for MVP. |
| User takes photo of someone else's journal | No way to detect this reliably. Accepted risk. The person still had to physically find a journal entry and photograph it. |
| User takes photo of a digital screen showing text | ML Kit may accept this. Partially mitigated by duplicate detection. Accepted risk for MVP. The friction of finding/creating such content is itself a deterrent. |

---

## 12. What is NOT in MVP

Explicitly out of scope to avoid feature creep:

- No user accounts or authentication
- No cloud backend or sync
- No subscription/payment system
- No OCR text extraction or analysis (just validation that text exists)
- No word clouds or sentiment analysis
- No social features or community
- No iOS version
- No monthly/yearly wrapped (only weekly)
- No custom themes or personalization
- No integration with other apps (health, fitness)
- No multi-language support (English only for v1)
- No data export
- No A/B testing framework

---

## 13. Technical Dependencies

```groovy
// build.gradle.kts (app)
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // CameraX
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // ML Kit (on-device text recognition)
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // WorkManager (for scheduled tasks)
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Coil (image loading in Compose)
    implementation("io.coil-kt:coil-compose:2.5.0")
}
```

---

## 14. Build & Distribution

### 14.1 MVP Distribution

- Generate signed APK for direct sideloading (no Play Store for MVP testing).
- Min SDK: 26 (Android 8.0) — covers 95%+ of active devices.
- Target SDK: 34 (Android 14).

### 14.2 Key Build Configurations

```groovy
android {
    namespace = "com.gratelock.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gratelock.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0-mvp"
    }
}
```

---

## 15. Success Metrics for MVP Validation

Before investing in a backend, subscriptions, or iOS, validate these:

| Metric | Target | How to Measure |
|---|---|---|
| Day 1 retention | >60% | User submits at least 1 entry on the day after install |
| Day 7 retention | >30% | User submits at least 1 entry in the 7th day after install |
| Average streak length | >5 days | Local analytics |
| Weekly wrap share rate | >20% | Track `wrap_shared` events vs `wrap_viewed` |
| Emergency unlock usage | <2x per user per month | Low usage = the blocking mechanic is working, not frustrating |
| Organic referrals | Any measurable | Track if wrap shares lead to installs (post-MVP: referral codes) |

---

## 16. Post-MVP Roadmap (For Context Only — Do Not Build)

This is included so the agent understands the direction and doesn't make architectural decisions that block future features.

1. **v0.2 — OCR & Insights**: Add Google ML Kit handwriting OCR. Extract text, generate word clouds, basic sentiment tracking. Store extracted text in `JournalEntry.ocrText`.
2. **v0.3 — Monthly/Yearly Wrapped**: Full Spotify-style wrapped experience. Top gratitude themes, mood trends, beautiful multi-card shareable stories.
3. **v0.4 — Backend & Auth**: FastAPI backend. User auth (Google Sign-In). Cloud photo storage (S3/R2). Cross-device sync. This enables the data pipeline.
4. **v0.5 — Subscriptions**: RevenueCat integration. Free tier (with anonymized data contribution) vs Premium (OCR insights, advanced stats, no data sharing). Target: $3.99/month or $29.99/year.
5. **v0.6 — Shake the Jar**: Shake phone → random past entry with animation. Emotional feature for retention.
6. **v1.0 — iOS**: Port to iOS using the Screen Time API (more restrictive, different blocking approach). Consider Kotlin Multiplatform or native Swift.
7. **v1.x — Social Features**: Anonymous community jar, friend challenges, group streaks.