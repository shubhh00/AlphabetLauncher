<div align="center">
  <h1>Alphabet Launcher</h1>
  <p><strong>A finger-tracking A–Z launcher for Android.</strong></p>
  <p>Browse installed apps with a custom bending alphabet, search with one swipe, and keep favourites close at hand.</p>
  <p>
    <img alt="Kotlin 2.2.10" src="https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&amp;logoColor=white" />
    <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack-Compose-4285F4?logo=jetpackcompose&amp;logoColor=white" />
    <img alt="Android 8.0 and later" src="https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&amp;logoColor=white" />
  </p>
  <p>
    <img alt="28 Canvas rows" src="https://img.shields.io/badge/Canvas%20rows-28-315E81" />
    <img alt="One pass app grouping" src="https://img.shields.io/badge/app%20grouping-1%20pass-315E81" />
    <img alt="Zero package queries on the gesture path" src="https://img.shields.io/badge/gesture%20path%20package%20queries-0-315E81" />
    <img alt="Minimum SDK 26" src="https://img.shields.io/badge/min%20SDK-26-315E81" />
  </p>
</div>

The selected letter stays open after release so an app can be tapped. Back or **Favourites** returns home; long-pressing an app updates favourites across restarts.

## Engineering at a glance

| Detail | Implementation |
| --- | --- |
| 26 letters, 28 drawn rows | A-Z plus two endpoint markers are rendered in one Canvas. |
| 1 pass to group apps by initial | The cached catalogue is grouped once when it changes, preserving the sorted app order within each letter. |
| Event-driven app refresh | The catalogue loads when the launcher becomes visible and refreshes after package add, remove, replace, or component-change broadcasts. |
| 0 package queries or icon conversions on the gesture path | Touch updates the curve and, when the letter changes, selects an already grouped list. A separate package-change refresh may still run concurrently. |
| In-memory search | Typing filters the cached app catalogue by name; it does not rescan installed packages. |
| 0 app-catalogue polling loops | A receiver is active only while the launcher is visible; missed changes are caught when it becomes visible again. |
| 0 external curve-animation libraries | Touch mapping and Gaussian falloff are implemented in project code; Compose supplies the Canvas and return spring. |
| Android 8.0+ | Minimum SDK 26; app discovery handles Android's package-visibility rules. |

These are implementation facts, not benchmark results. Frame rate, frame time, and app-load latency have not yet been measured on a device.

## Screenshots

| Home | Alphabet browse | Swipe-up search |
| :---: | :---: | :---: |
| <img src="docs/screenshots/home.png" alt="Home screen with clock, favourites, and alphabet bar" width="220" /> | <img src="docs/screenshots/browse.png" alt="Browse screen with the G selection and matching apps" width="220" /> | <img src="docs/screenshots/search.png" alt="Search screen filtering apps by name" width="220" /> |
| Clock and favourites | Finger-tracking A-Z bar and app list | In-memory app search |

## Demo video

**Video pending.** Add the recorded launcher walkthrough here after capturing it on a phone. Show the alphabet drag and spring return, app launch, swipe-up search, favourite toggle, and live install/uninstall update.

## Run

1. Open this folder in Android Studio.
2. Let Gradle sync with the versions in `gradle/libs.versions.toml`.
3. Run the `app` configuration on an Android phone running Android 8.0 (API 26) or later.

The app queries activities with `ACTION_MAIN` and `CATEGORY_LAUNCHER`. The manifest declares that intent in `<queries>` for Android 11+ package visibility. `PackageManager` runs on an IO thread when the launcher becomes visible or a package changes. The resulting app list is cached in activity state and grouped A-Z before interaction. Each entry keeps the activity's component and icon so a tap opens the exact activity shown.

## Curve animation

`AlphabetBar.kt` draws all 28 rows (star, A-Z, dot) in one Canvas. A pointer's vertical position selects a letter. Each row moves left by a Gaussian falloff based on its distance from the pointer (`bendOffset` in `AlphabetMath.kt`). Pointer movement updates the bar position, and a letter change updates the visible list and haptic feedback; no package query or icon loading happens in the gesture handler. On release, a Compose spring animates the bend amount to zero. The bubble shows the selected letter while the pointer is down. The curve and touch mapping are implemented in this project rather than supplied by an animation library.

## Structure and bonuses

`MainActivity.kt` hosts the app and handles launching. The `data` package loads installed apps, watches package changes, and saves favourites as component names in SharedPreferences. The `ui` package composes the launcher screen and its state; `ui.alphabet` owns pointer handling, Canvas drawing, and pure selection and falloff calculations; `ui.search` owns the swipe gesture and search view; `ui.components` holds the clock and app list; `ui.theme` holds system-aware colours and typography. The first seven installed apps become the initial favourites only once; later changes survive restarts.

The bar gives a light haptic tick when the selected letter changes. Letters with no installed apps are dimmed but remain selectable so the empty state can be demonstrated. The launcher follows the system light or dark setting with a subtle gradient background; system bar icons follow the same setting.

The home and letter views use the same restrained type hierarchy and lightly outlined app rows. The clock refreshes at minute boundaries because seconds are not displayed. These visual changes leave the Canvas gesture and cached app list unchanged.

Swiping up on unused Home space opens search and focuses the text field. The swipe observer ignores drags consumed by the favourites list and excludes the alphabet bar. A visible search hint is also tappable. Search results reuse the same app rows, including tap-to-launch and long-press favourites.

The app also registers as a Home app. On first launch, it asks whether to open Android's default-launcher picker. Choosing "Not now" keeps the current launcher and stops the first-launch prompt; the home screen still offers a "Set as default launcher" action. The choice is made by the Android system, not by the app itself.

Live updates use a context-registered receiver for package add, remove, replace, and component-change broadcasts. A short debounce coalesces bursts of install events. When the launcher returns from the background, it reloads to catch changes that occurred while the receiver was stopped. Existing favourite choices remain in state during a refresh.

## Libraries

The app uses AndroidX and Compose dependencies from the starter project. Each runtime dependency has a specific job; the curve itself is implemented without an external animation package.

| Library | Version | Purpose |
| --- | --- | --- |
| Android Gradle Plugin | 9.2.1 | Android build tooling |
| Kotlin and Compose compiler plugin | 2.2.10 | Kotlin and Compose compilation |
| Compose BOM | 2026.02.01 | Align Compose library versions |
| `androidx.activity:activity-compose` | 1.13.0 | Compose activity host |
| `androidx.compose.ui:ui` and `ui-graphics` | Compose BOM | UI and Canvas drawing |
| `androidx.compose.material3:material3` | Compose BOM | Theme and text |
| `androidx.core:core-ktx` | 1.19.1 | Drawable bitmap conversion |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.11.0 | Lifecycle coroutine scope |
| JUnit | 4.13.2 | Local tests |

Other starter dependencies (`ui-tooling`, `ui-tooling-preview`, Compose UI test, Espresso, AndroidX test) remain in `app/build.gradle.kts` for development and tests; their versions are in `gradle/libs.versions.toml` or set by the Compose BOM.

## Verification

Run `./gradlew testDebugUnitTest assembleDebug` after Gradle sync. On a phone, check the full A-Z drag, an empty letter, release, haptics, both system themes, app launching, favourite persistence after a restart, both default-launcher choices, live changes after installing or removing another app, and swipe-up search with the keyboard visible. Confirm the bend and letter bubble appear on the first touch from Home, including after returning from Browse. Scrolling favourites and dragging the alphabet should not open search. Smoothness still needs a measured frame-rate check on a device.

## Notes

OpenAI Codex provided substantial assistance with code drafting, debugging, refactoring, and documentation. The project owner set up the project, directed features and design, and tested the app on a phone; final code review and explanation remain the owner's responsibility.
