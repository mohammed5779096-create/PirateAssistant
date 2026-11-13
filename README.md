# Pirate Assistant v1.0

Native Android starter project (Kotlin + Jetpack Compose).

## Build
1. Open this folder in Android Studio.
2. Let Gradle sync.
3. Build > Build Bundle(s) / APK(s) > Build APK(s).

## Current features
- Dark Pirate Assistant dashboard.
- Launch Fortnite if the standard package ID is installed.
- Optional Android Usage Access permission screen.
- Best-effort recent app usage detection.
- Safe performance guidance and manual Ready reminder concept.

## Important limitations
- Android usage stats are not a guaranteed real-time foreground-app API. Some devices and Android versions may report delayed or incomplete data.
- Fortnite package IDs can vary by distribution/version; the launch button may report not found.
- This app does not automate gameplay, click Ready, modify Fortnite, bypass AFK controls, or promise FPS gains.
- Premium/payment, persistent match history, reliable thermal readings, and notifications are not implemented in v1.
- Do not sell this as a finished performance booster. Test on the target device first.

## Build without a PC
Upload this project to a GitHub repo, open Actions > Build APK > Run workflow, then download the APK artifact.
