# Screen Touch Locker

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.handysparksoft.screentouchlocker">
    <img alt="Get it on Google Play"
         src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png"
         height="76" />
  </a>
</p>

<p align="center">
  <a href="https://github.com/davidasensio/ScreenTouchLocker/actions/workflows/android.yml"><img alt="Android CI"
    src="https://github.com/davidasensio/ScreenTouchLocker/actions/workflows/android.yml/badge.svg" /></a>
</p>

<p align="center">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.2-7F52FF?logo=kotlin&logoColor=white" />
  <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?logo=jetpackcompose&logoColor=white" />
  <img alt="min SDK 29" src="https://img.shields.io/badge/min_SDK-29-3DDC84?logo=android&logoColor=white" />
  <img alt="2 languages" src="https://img.shields.io/badge/languages-2-informational" />
</p>

An Android app that locks your screen against unintended touches, so you can hand the phone to a
child, clean it, or watch something without the display reacting to every tap. The lock is a
transparent overlay you dismiss with a deliberate long press — it does not turn the screen off, so
whatever you were watching keeps playing underneath.

<p align="center">
  <img alt="Screen Touch Locker tour" src="./screen_touch_locker_tour.gif" width="300" />
</p>

## Features

- ✅ Lock the screen against touches with a full-screen transparent overlay, dismissed by a long
  press on the unlock button so it cannot be undone by accident
- ✅ Optional visual feedback showing where blocked touches land, plus haptic feedback
- ✅ Shake to lock and shake to unlock, both independently switchable
- ✅ Quick Settings tile, with a one-tap "add the tile" action on Android 13+ instead of the
  manual Edit Tiles flow
- ✅ Ongoing notification with Lock / Unlock / Turn off actions
- ✅ Starts automatically after a device reboot
- ✅ Onboarding carousel explaining each way to trigger the lock
- ✅ Light and dark theme, localized into English and Spanish

## Tech Stack

- Kotlin, Jetpack Compose (Material 3) for the onboarding and settings UI
- Classic Android Views for the lock overlay itself — it is added straight to the
  `WindowManager` as a `TYPE_APPLICATION_OVERLAY` window, which is not something Compose hosts well
- A `specialUse` foreground service owning the overlay and the ongoing notification
- `TileService` for the Quick Settings tile, `seismic` for shake detection
- GitHub Actions for CI, spotless + ktlint for formatting

## Getting Started

- **Requirements**: Android Studio (current stable), JDK 17, Android SDK with API 37 installed
- **Min SDK**: 29 (Android 10) · **Target SDK**: 36 · **Compile SDK**: 37

```bash
./gradlew assembleDebug
```

The app needs two permissions to do anything useful, both requested from the onboarding screen:
**Display over other apps** (the overlay is the whole point) and **notifications** (the foreground
service must show one).

## Build Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Tests
./gradlew test                      # Unit tests
./gradlew connectedAndroidTest      # Instrumented tests (needs a device/emulator)

# Static analysis
./gradlew spotlessCheck             # ktlint via spotless — this is what CI runs
./gradlew spotlessApply             # Auto-format
./gradlew lint                      # Android lint
```

## Releasing

`assembleRelease` and `bundleRelease` read signing material from `keystore/`, which is git-ignored
and absent on a fresh clone. Without it the build still succeeds — it prints a warning and produces
an unsigned artifact — so a release build that "worked" is not evidence it can be uploaded.

Two things worth checking before shipping, both learned the hard way:

- **Install and launch the release build.** Minification is enabled, and R8 can break code that
  compiles and passes every test.
- **Exercise the service on a recent Android version.** Most of this app's production crashes have
  come from foreground service and overlay policy changes rather than from its own logic, and those
  only show up at runtime. Reboot the device, lock and unlock, and tap the tile.

## Project Structure

Single module, packages by responsibility:

```
app/
 ├── ScreenTouchLockerService     ← foreground service, owns the overlay and its notification
 ├── ScreenTouchLockerTileService ← Quick Settings tile
 ├── ShakeDetectorService         ← shake detection
 ├── BootReceiver                 ← restarts the service after a reboot
 ├── classic/                     ← the lock overlay window (Android Views)
 ├── ui/                          ← Compose onboarding, config options, theme
 └── platform/                    ← preferences, in-app review
```

## Credits

This app takes as a reference a good part of these posts from Václav Hodek (@vaclavhodek):

- [Floating windows on Android 1: Jetpack Compose, Room](https://medium.com/localazy/floating-windows-on-android-1-jetpack-compose-room-a4f377c86dd5)
- [Floating windows on Android 2: Foreground service](https://localazy.com/blog/floating-windows-on-android-2-foreground-service)

## Author

David Asensio — HandySparkSoft 

© 2022-2026 ScreenTouchLocker
