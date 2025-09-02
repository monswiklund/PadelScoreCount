package com.example.padel3.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme

// Definierar ett anpassat tema för appen med namnet Padel3Theme
@Composable
fun Padel3Theme(content: @Composable () -> Unit) {
    // Använder MaterialTheme som en bas för temat, vilket applicerar Wear OS-specifika
    // färger, typografi och andra stilar för appens gränssnitt
    MaterialTheme {
        // content() är en lambda som kapslar in det UI som ska använda detta tema
        content()
    }
}