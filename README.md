<div align="center">
  <h1>Alphabet Launcher</h1>
  <p><strong>A finger-tracking A–Z launcher for Android.</strong></p>
  <p>Browse installed apps with a custom bending alphabet, search with one swipe, and keep favourites close at hand.</p>
  <p>
    <img alt="Kotlin 2.2.10" src="https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&amp;logoColor=white" />
    <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack-Compose-4285F4?logo=jetpackcompose&amp;logoColor=white" />
    <img alt="Android 8.0 and later" src="https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&amp;logoColor=white" />
  </p>
</div>

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

<table align="center">
  <tr>
    <th align="center">Home</th>
    <th align="center">Alphabet browse</th>
    <th align="center">Swipe-up search</th>
  </tr>
  <tr>
    <td align="center"><img src="https://github.com/user-attachments/assets/d91858aa-e48a-4365-8ad5-e7addde10b3b" alt="Home screen with clock, favourites, and alphabet bar" width="220" /></td>
    <td align="center"><img src="https://github.com/user-attachments/assets/655a9c6d-7d5b-4f62-9759-418e9d0bb89f" alt="Browse screen with the G selection and matching apps" width="220" /></td>
    <td align="center"><img src="https://github.com/user-attachments/assets/386f7178-316a-4fe8-a3bd-ff70488b24b2" alt="Search screen filtering apps by name" width="220" /></td>
  </tr>
  <tr>
    <td align="center">Clock and favourites</td>
    <td align="center">Finger-tracking A–Z bar and app list</td>
    <td align="center">In-memory app search</td>
  </tr>
</table>

## Demo video

<div align="center">
  <video src="https://github.com/user-attachments/assets/5c495181-f7c7-41f0-8405-508bc084b4fa" width="320" controls></video>
  <p><sub>See the alphabet respond to touch, browse apps by letter, and open search with a swipe.</sub></p>
</div>

## Run

1. Open this folder in Android Studio.
2. Let Gradle sync with the versions in `gradle/libs.versions.toml`.
3. Run the `app` configuration on an Android phone running Android 8.0 (API 26) or later.

The app queries activities with `ACTION_MAIN` and `CATEGORY_LAUNCHER`. The manifest declares that intent in `<queries>` for Android 11+ package visibility. `PackageManager` runs on an IO thread when the launcher becomes visible or a package changes. The resulting app list is cached in activity state and grouped A-Z before interaction. Each entry keeps the activity's component and icon so a tap opens the exact activity shown.

## Curve animation

`AlphabetBar.kt` draws all 28 rows (star, A-Z, dot) in one Canvas. A pointer's vertical position selects a letter. Each row moves left by a Gaussian falloff based on its distance from the pointer (`bendOffset` in `AlphabetMath.kt`). Pointer movement updates the bar position, and a letter change updates the visible list and haptic feedback; no package query or icon loading happens in the gesture handler. On release, a Compose spring animates the bend amount to zero. The bubble shows the selected letter while the pointer is down. The curve and touch mapping are implemented in this project rather than supplied by an animation library.

## Structure and bonuses

`MainActivity.kt` hosts the launcher. `data` handles app discovery, package-change updates, and persisted favourites; `ui.alphabet` contains the touch and Canvas logic; `ui.search` handles swipe-up search; `ui.components` and `ui.theme` contain shared views and system-aware styling.

Bonuses include haptic ticks, dimmed empty letters, light and dark themes, long-press favourites, live app-list updates, and keyboard-focused search. The first launch offers Android's default-launcher picker, with a later option on Home. Selecting a letter keeps its app list open for tapping; Back or **Favourites** returns Home.

The UI uses a subtle gradient, a clear type hierarchy, and lightly outlined app cards to give the launcher a clean look in both light and dark themes. The clock, favourites, browse view, and search screen share the same visual style.

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
