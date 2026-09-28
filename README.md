# Alphabet Launcher

A small Kotlin and Jetpack Compose launcher inspired by the assignment reference. The resting screen shows a live clock, date, and favourite apps. Touch the alphabet on the right to browse apps by their first letter. The selected list stays open after release so you can tap an app; use Back or “Favourites” to return home. Long-pressing an app adds or removes it from favourites.

## Run

1. Open this folder in Android Studio.
2. Let Gradle sync with the versions in `gradle/libs.versions.toml`.
3. Run the `app` configuration on an Android phone running Android 8.0 (API 26) or later.

The app queries activities with `ACTION_MAIN` and `CATEGORY_LAUNCHER`. The manifest declares that intent in `<queries>` for Android 11+ package visibility. `PackageManager` is queried once when the activity starts, on an IO thread. The resulting app list is cached in activity state and grouped A-Z before interaction. Each entry keeps the activity's component and icon so a tap opens the exact activity shown.

## Curve animation

`AlphabetBar.kt` draws all 28 rows (star, A-Z, dot) in one Canvas. A pointer's vertical position selects a letter. Each row moves left by a Gaussian falloff based on its distance from the pointer (`bendOffset` in `AlphabetMath.kt`). The touch position changes only the Canvas state; no package query or icon loading happens in the gesture handler. On release, a Compose spring animates the bend amount to zero. The bubble shows the selected letter while the pointer is down.

## Structure and bonuses

`MainActivity.kt` hosts the app and handles launching. The `data` package loads installed apps and saves favourites as component names in SharedPreferences. The `ui` package contains the launcher screen, clock, list, alphabet bar, and its selection helpers; `ui.theme` holds the system-aware colours and typography. The first seven installed apps become the initial favourites only once; later changes survive restarts.

The bar gives a light haptic tick when the selected letter changes. Letters with no installed apps are dimmed but remain selectable so the empty state can be demonstrated. The launcher follows the system light or dark setting with a subtle gradient background; system bar icons follow the same setting.

The app also registers as a Home app. On first launch, it asks whether to open Android's default-launcher picker. Choosing "Not now" keeps the current launcher and stops the first-launch prompt; the home screen still offers a "Set as default launcher" action. The choice is made by the Android system, not by the app itself.

## Libraries

These are the dependencies already configured by the Android Studio starter project. The app adds no animation library.

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

## AI disclosure

OpenAI Codex helped implement and refactor the launcher code, gesture math, themes, haptics, favourites, Home role flow, README, and tests. Review and understand the code before submitting it, and make any changes needed after testing on your own phone.

## Testing and submission

Run `./gradlew testDebugUnitTest assembleDebug` after Gradle sync. Test the full A-Z drag, an empty letter, release, haptics, both system themes, app launching, long-pressing to add and remove favourites, persistence after a restart, and both default-launcher choices on your phone. Record the required 1-3 minute video on that phone and publish the repository yourself. This project has not been measured at 60 fps on a device yet.
