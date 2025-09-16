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

The project follows Clean Architecture principles with 42 Kotlin files organized in three main layers:

```
app/src/main/java/com/example/padel3/
├── data/                           # Data Layer
│   ├── preferences/
│   │   └── PreferencesManager.kt   # DataStore preference management
│   └── repository/
│       ├── GameRepositoryImpl.kt   # Game state repository implementation
│       └── MatchHistoryRepositoryImpl.kt # Match history repository implementation
│
├── domain/                         # Domain Layer (Business Logic)
│   ├── model/
│   │   ├── GameMode.kt            # Game mode definitions
│   │   ├── GameState.kt           # Core game state model
│   │   ├── MatchHistory.kt        # Match history data model
│   │   ├── MatchResult.kt         # Match result types
│   │   └── ScoreEvent.kt          # Score event definitions
│   ├── repository/
│   │   ├── GameRepositoryInterface.kt # Game repository contract
│   │   └── MatchHistoryRepository.kt  # Match history repository contract
│   └── usecase/
│       ├── DeleteMatchUseCase.kt   # Delete match functionality
│       ├── GetMatchHistoryUseCase.kt # Retrieve match history
│       ├── IncrementScoreUseCase.kt # Score increment logic
│       ├── ResetGameUseCase.kt     # Game reset functionality
│       ├── SaveMatchUseCase.kt     # Save match to history
│       └── UndoLastActionUseCase.kt # Undo functionality
│
└── presentation/                   # Presentation Layer (UI)
    ├── components/                 # Reusable UI components
    │   ├── GemCounter.kt          # Score gem visualization
    │   ├── GlassButton.kt         # Glass effect buttons
    │   ├── MatchHistoryComponents.kt # Match history UI components
    │   ├── MiddleLine.kt          # Court middle line component
    │   ├── PlayerArea.kt          # Player score area
    │   ├── ScoreDisplay.kt        # Score display component
    │   └── ServingEdgeIndicator.kt # Serving indicator
    ├── screens/                    # Full screen composables
    │   ├── MainMenuScreen.kt      # Main menu navigation
    │   ├── MatchHistoryScreen.kt  # Match history display
    │   ├── ModeSelectionScreen.kt # Game mode selection
    │   ├── ScoreScreen.kt         # Main scoring interface
    │   └── ServeSelectionScreen.kt # Initial serve selection
    ├── theme/
    │   └── Theme.kt               # Compose theme definitions
    ├── utils/
    │   ├── AnimationHelper.kt     # Animation utilities
    │   └── VibrationHelper.kt     # Haptic feedback utilities
    ├── viewmodel/
    │   ├── MatchHistoryViewModel.kt # Match history business logic
    │   └── ScoreViewModel.kt      # Main scoring business logic
    ├── PadelScoreActivity.kt      # Main activity with wake lock
    ├── ScoreCounter.kt            # Legacy main UI component
    ├── ModeSelector.kt            # Legacy mode selection
    └── WearApp.kt                 # Root composable with theming
```

## License

This project is for personal use.