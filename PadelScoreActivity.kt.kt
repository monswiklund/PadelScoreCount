package com.example.padel3.presentation

import android.os.Bundle
import android.os.PowerManager
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

// Huvudaktiviteten för appen som hanterar skärmens wake lock och vibration
class PadelScoreActivity : ComponentActivity() {
    private lateinit var wakeLock: PowerManager.WakeLock // Wake lock för att hålla skärmen på
    private lateinit var vibrator: Vibrator // Vibrator för att skapa vibrationseffekter

    override fun onCreate(savedInstanceState: Bundle?) {
        // Installera en splash screen som visas när appen startas
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Hämtar tjänsten för vibration från systemet
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        // Hämtar PowerManager-tjänsten för att skapa en wake lock
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager

        // Skapar en wake lock som håller skärmen ljus och väcker den om den är släckt
        wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "Padel3::MyWakeLock"
        )
        // Aktiverar wake lock för en bestämd tid (120 minuter)
        wakeLock.acquire(120 * 60 * 1000L)

        // Sätter innehållet för aktivitetsvyn och startar WearApp UI-komponenten
        setContent {
            WearApp(vibrator = vibrator, appName = "")
        }
    }

    // Frigör wake lock när aktiviteten förstörs för att undvika att hålla skärmen tänd i onödan
    override fun onDestroy() {
        super.onDestroy()
        wakeLock.release()
    }
}