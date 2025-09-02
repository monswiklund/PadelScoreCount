package com.example.padel3.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Definierar gradienter som används i UI-komponenter.
 * Dessa gradienter används för att ge knapparna olika tydliga färger.
 */

// Skapar en vertikal röd gradient, från ljusare röd till mörkare nyanser av röd
val redGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFA5A3D), // Ljusare röd
        Color(0xFFFF4500), // Mellanröd
        Color(0xFF8B0000)  // Mörkare röd
    )
)

// Skapar en vertikal blå gradient, från mörkare blå till ljusare nyanser av blå
val blueGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF01238B),  // Mörkare blå
        Color(0xFF3372E0),  // Blå
        Color(0xFF3996E0)   // Ljusare blå
    )
)

// Skapar en vertikal grå gradient, från mörkare grå till ljusare nyanser av grå
val greyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF3C3C3C),  // Mörkare grå
        Color(0xFF5A5A5A),  // Mellangrå
        Color(0xFF787878)   // Ljusare grå
    )
)

// Skapar en horisontell gradient från röd till blå, för full-screen övergång
val horizontalRedToBlueGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFF4500), // Röd på toppen
        Color(0xFF3372E0)  // Blå på botten
    )
)