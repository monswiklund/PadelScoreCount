# Padel Score Counter - TODO & Improvement Plan

## 🚨 Critical Issues (Fix First)

### Technical Debt
- [ ] **Fix file naming issue**: Rename `PadelScoreActivity.kt.kt` to `PadelScoreActivity.kt`
  - Location: `app/src/main/java/com/example/padel3/presentation/PadelScoreActivity.kt.kt`
  - Impact: Confusing file structure, potential build issues

- [ ] **Update deprecated APIs**
  - Replace `VIBRATOR_SERVICE` with `VibratorManager` (API 31+)
  - Update `SCREEN_BRIGHT_WAKE_LOCK` to `SCREEN_DIM_WAKE_LOCK` 
  - Update `ACQUIRE_CAUSES_WAKEUP` usage
  - Location: `PadelScoreActivity.kt:34,41`

- [ ] **Clean up unused code**
  - Remove unused parameter `appName` in `ScoreCounter.kt:42`
  - Remove unused variable `isPlayerOneServing` in `ScoreCounter.kt:59`
  - Remove unused parameter `appName` in `WearApp.kt:16`
  - Remove unused parameter `gameMode` in `GameOverlays.kt:44`
  - Remove unused variable `otherPlayerScore` in `IncrementScoreUseCase.kt:20`

## 🚀 High Priority Features

### 1. Enhanced Scoring Display
- [ ] **Add "Advantage" indicator for Vinnarbana mode**
  - When score is 40-40 (deuce), show "ADV" instead of score for leading player
  - Implementation: Update `ScoreScreen.kt` and `GameState.kt`
  - Estimated effort: 2-3 hours

- [ ] **Match/Set Point indicators**
  - Visual indicator when player is one point away from winning game/set/match
  - Add pulsing animation or color change
  - Location: Update `PlayerArea.kt` component
  - Estimated effort: 3-4 hours

- [ ] **Customizable Mexicano point limits**
  - Add settings for 16, 24, 32, or custom point limits
  - Implement settings screen with DataStore persistence
  - Update `GameState.kt` and `ResetGameUseCase.kt`
  - Estimated effort: 4-5 hours

### 2. User Experience Improvements
- [ ] **Celebration animations**
  - Add confetti or pulse animations for game/set/match wins
  - Use Compose animations with `AnimatedVisibility`
  - Location: Create new `CelebrationOverlay.kt` component
  - Estimated effort: 3-4 hours

- [ ] **Custom vibration patterns**
  - Different patterns for: point scored, game won, set won, match won
  - Implement in `VibrationHelper.kt`
  - Add user customization options
  - Estimated effort: 2-3 hours

- [ ] **Gesture controls**
  - Swipe left/right to undo/redo
  - Long press for reset options menu
  - Implement using Compose gesture detection
  - Estimated effort: 4-5 hours

### 3. Visual Polish
- [ ] **Theme system**
  - Dark/light theme toggle
  - High contrast mode for outdoor visibility
  - Update `theme.kt` and add theme switching logic
  - Estimated effort: 3-4 hours

- [ ] **Better outdoor visibility**
  - Increase contrast ratios
  - Add brightness boost option
  - Larger touch targets for easier interaction
  - Estimated effort: 2-3 hours

## 📊 Medium Priority Features

### 4. Statistics & History
- [ ] **Match history database**
  - Implement Room database for match storage
  - Schema: Match, Player, Score history
  - Create repository layer
  - Estimated effort: 6-8 hours

- [ ] **Statistics dashboard**
  - Win/loss ratios per game mode
  - Average match duration
  - Most frequent opponents
  - Longest winning/losing streaks
  - Estimated effort: 5-6 hours

- [ ] **Export functionality**
  - Export match data to CSV/JSON
  - Share via email or cloud storage
  - Implement using Android sharing intents
  - Estimated effort: 3-4 hours

### 5. Enhanced Gameplay Features
- [ ] **Tournament bracket support**
  - Create tournament with multiple matches
  - Track bracket progression
  - Winner determination logic
  - New UI screens for tournament management
  - Estimated effort: 8-10 hours

- [ ] **Match configurations**
  - Best-of-3 or best-of-5 sets
  - No-advantage scoring option
  - Short sets (first to 4 games)
  - Super tiebreak (first to 10 points)
  - Estimated effort: 4-6 hours

- [ ] **Timer functionality**
  - Practice session timer
  - Between-point countdown timer
  - Match duration tracking
  - Estimated effort: 3-4 hours

### 6. Team/Player Management
- [ ] **Player profiles**
  - Save player names and statistics
  - Profile pictures (optional)
  - Playing style preferences
  - Estimated effort: 5-6 hours

- [ ] **Doubles support**
  - Team name entry
  - Team statistics
  - Serving order management
  - Estimated effort: 4-5 hours

## 🔧 Technical Improvements

### 7. Architecture Enhancements
- [ ] **Dependency injection with Hilt**
  - Add Hilt dependencies to `build.gradle.kts`
  - Convert use cases to Hilt modules
  - Update ViewModels with @HiltViewModel
  - Estimated effort: 4-6 hours

- [ ] **Comprehensive error handling**
  - Try-catch blocks for critical operations
  - User-friendly error messages
  - Crash reporting integration
  - Estimated effort: 3-4 hours

- [ ] **Proper loading states**
  - Loading indicators for async operations
  - Skeleton screens for better UX
  - Update UI components with loading states
  - Estimated effort: 2-3 hours

### 8. Data Persistence
- [ ] **Auto-save functionality**
  - Save game state on app pause/backgrounding
  - Restore state on app resume
  - Use DataStore for preferences
  - Estimated effort: 3-4 hours

- [ ] **Settings screen**
  - Vibration preferences
  - Theme selection
  - Default game mode
  - Sound effects toggle
  - Estimated effort: 4-5 hours

- [ ] **Backup & restore**
  - Export/import app data
  - Cloud backup integration
  - Data migration between devices
  - Estimated effort: 6-8 hours

### 9. Testing & Quality
- [ ] **Expand unit test coverage**
  - Test all use cases thoroughly
  - ViewModel testing with edge cases
  - Add parameterized tests for scoring logic
  - Target: >80% code coverage
  - Estimated effort: 5-7 hours

- [ ] **UI/Integration tests**
  - Compose UI testing
  - End-to-end scoring scenarios
  - Mode switching tests
  - Estimated effort: 4-6 hours

- [ ] **Performance optimization**
  - Profile app with Android Studio profiler
  - Optimize Compose recomposition
  - Memory leak detection
  - Battery usage optimization
  - Estimated effort: 3-5 hours

## 🌐 Future Enhancements (Low Priority)

### 10. Connectivity & Sharing
- [ ] **Phone companion app**
  - Larger display on phone
  - Bluetooth sync between devices
  - Remote control functionality
  - Estimated effort: 10-15 hours

- [ ] **Social features**
  - Share match results on social media
  - Challenge friends functionality
  - Leaderboards
  - Estimated effort: 8-12 hours

- [ ] **Smart integrations**
  - Google Fit integration for calories
  - Weather API for outdoor conditions
  - Calendar integration for scheduled matches
  - Estimated effort: 6-10 hours

### 11. Accessibility
- [ ] **Screen reader support**
  - Content descriptions for all UI elements
  - Semantic markup for Compose components
  - Voice announcements for score changes
  - Estimated effort: 4-6 hours

- [ ] **Voice commands**
  - "Player one scores" voice command
  - "Undo last point" command
  - Speech recognition integration
  - Estimated effort: 6-8 hours

- [ ] **Large text support**
  - Respect system font size settings
  - Dynamic text sizing options
  - Improved contrast ratios
  - Estimated effort: 2-3 hours

### 12. Advanced Features
- [ ] **Watch face complications**
  - Show current score on watch face
  - Quick access to app from complications
  - Wear OS Tiles integration
  - Estimated effort: 5-7 hours

- [ ] **Coaching features**
  - Shot tracking (winners, errors)
  - Basic analytics
  - Performance insights
  - Estimated effort: 8-12 hours

## 📅 Implementation Timeline Suggestion

### Phase 1 (Week 1-2): Critical Fixes
- Fix file naming and deprecated APIs
- Clean up unused code
- Add advantage indicator
- Basic celebration animations

### Phase 2 (Week 3-4): Core UX Improvements
- Custom vibration patterns
- Theme system
- Gesture controls
- Match/set point indicators

### Phase 3 (Week 5-8): Data & History
- Match history database
- Statistics dashboard
- Auto-save functionality
- Settings screen

### Phase 4 (Week 9-12): Advanced Features
- Tournament support
- Enhanced testing
- Performance optimization
- Accessibility improvements

## 📝 Notes

- All time estimates are for a single developer
- Consider user feedback before implementing low-priority features
- Maintain backward compatibility with existing saved states
- Test thoroughly on actual Wear OS devices, not just emulator
- Consider battery impact of new features
- Follow Material Design guidelines for Wear OS

## 🔄 Review Process

- [ ] Code review required for all major features
- [ ] Test on multiple Wear OS device sizes
- [ ] Performance testing before each release
- [ ] User acceptance testing with actual padel players
- [ ] Documentation updates for new features