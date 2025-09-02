package com.example.padel3.presentation

import android.os.Vibrator
import androidx.compose.runtime.Composable
import com.example.padel3.presentation.theme.Padel3Theme

/**
 * Huvudkompositionen för Wear-appen som applicerar temat och visar huvudkomponenten, ScoreCounter.
 * Fungerar som main entry-point för UI.
 */
@Composable
fun WearApp(vibrator: Vibrator, appName: String, viewModel: ScoreViewModel) {
    // Använder Padel3Theme för att applicera appens tema på UI
    Padel3Theme {
        // Kallar ScoreCounter-komponenten och skickar vibrator, appnamn och viewModel som argument
        ScoreCounter(
            vibrator = vibrator,
            appName = appName,
            viewModel = viewModel
        )
    }
}