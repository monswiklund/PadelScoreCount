# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android Wear OS application for tracking padel match scores, built with Kotlin and Jetpack Compose. The app is designed to run as a standalone wearable application that manages tennis/padel scoring with support for sets, games, tiebreaks, and serve tracking.

## Architecture

The application follows a clean MVVM architecture with the following structure:

- **Presentation Layer** (`app/src/main/java/com/example/padel3/presentation/`):
  - `PadelScoreActivity.kt.kt` - Main activity with wake lock management and vibration setup
  - `WearApp.kt` - Root composable that applies theming
  - `ScoreCounter.kt` - Main UI component for score display and interaction
  - `ModeSelector.kt` - Game mode selection UI component
  - `ScoreViewModel.kt` - Business logic for score calculation and game state management with dual-mode support
  - `theme/theme.kt` - Compose theme definitions
  - `Gradients.kt` - Gradient definitions for UI styling

- **Core Features**:
  - **Dual Game Modes**: 
    - Vinnarbana (Winner Court): Traditional tennis scoring (0, 15, 30, 40) with sets and games
    - Mexicano: Point-based scoring system (default 24 points per match)
  - Mode selection screen shown at startup and when resetting matches
  - Set and game tracking with proper win conditions (Vinnarbana mode)
  - Tiebreak support with alternating serve tracking (Vinnarbana mode)
  - Undo functionality with state history (max 20 states)
  - Wake lock management to keep screen active during matches
  - Vibration feedback for user interactions
  - Mode indicator showing current game format

## Development Commands

This project uses Gradle with Kotlin DSL for build management:

```bash
# Build the project
./gradlew build

# Build and install debug APK to connected device
./gradlew installDebug

# Clean build artifacts
./gradlew clean

# Run lint checks
./gradlew lint

# Generate release APK
./gradlew assembleRelease
```

## Key Configuration

- **Target SDK**: 34 (Android 14)
- **Min SDK**: 30 (Android 11 - required for Wear OS)
- **Namespace**: `com.example.padel3`
- **Wear OS**: Standalone app (doesn't require phone companion)
- **Compose**: Uses Wear Compose with Material design components

## Dependencies

The project uses Wear OS specific libraries defined in `gradle/libs.versions.toml`:
- Wear Compose Material and Foundation
- Google Play Services Wearable
- Core Splash Screen
- Activity Compose
- Standard Compose UI toolkit

## Important Implementation Details

- **Wake Lock Management**: The app uses `PowerManager.WakeLock` with a 2-hour timeout to keep the screen on during matches
- **Vibration**: Integrated vibration feedback for user interactions
- **State Management**: Uses Compose state with undo functionality and proper lifecycle management
- **Scoring Logic**: Implements proper tennis scoring rules including tiebreaks at 6-6 games
- **Serve Tracking**: Alternates serving players, with special handling during tiebreaks

## File Naming Note

There's a naming inconsistency: `PadelScoreActivity.kt.kt` has double `.kt` extension - this should be corrected to `PadelScoreActivity.kt` when making changes.