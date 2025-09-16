# Padel Score Count

A Wear OS application for tracking padel match scores, built with Kotlin and Jetpack Compose.

## Overview

This standalone Wear OS app manages tennis/padel scoring with support for two game modes:

- **Vinnarbana (Winner Court)**: Traditional tennis scoring (0, 15, 30, 40) with sets, games, and tiebreaks
- **Mexicano**: Point-based scoring system (default 24 points per match)

## Features

- Dual game mode support with mode selection at startup
- Set and game tracking with proper win conditions
- Tiebreak support with alternating serve tracking
- Undo functionality with state history (max 20 states)
- Wake lock management to keep screen active during matches
- Vibration feedback for user interactions
- Mode indicator showing current game format

## Requirements

- **Target SDK**: 34 (Android 14)
- **Min SDK**: 30 (Android 11 - required for Wear OS)
- **Platform**: Wear OS standalone app (no phone companion required)

## Development

### Build Commands

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

### Architecture

The application follows MVVM architecture with:

- **Presentation Layer**: Compose UI components and ViewModels
- **Core Features**: Dual game modes, scoring logic, and state management
- **Wear OS Integration**: Wake lock, vibration, and wearable-specific UI

### Key Dependencies

- Wear Compose Material and Foundation
- Google Play Services Wearable
- Core Splash Screen
- Activity Compose
- Standard Compose UI toolkit

## Project Structure

```
app/src/main/java/com/example/padel3/
├── presentation/
│   ├── PadelScoreActivity.kt    # Main activity
│   ├── WearApp.kt              # Root composable
│   ├── ScoreCounter.kt         # Main UI component
│   ├── ModeSelector.kt         # Game mode selection
│   └── ScoreViewModel.kt       # Business logic
└── domain/
    └── model/                  # Data models
```

## License

This project is for personal use.