package com.example.padel3.presentation

import android.os.Bundle
import android.os.PowerManager
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

/**
 * Huvudaktiviteten för appen som hanterar skärmens wake lock och vibration.
 * Initierar och konfigurerar alla appens komponenter.
 */
class PadelScoreActivity : ComponentActivity() {
    // Konstant för wakeLock-tidshantering
    companion object {
        private const val DEFAULT_WAKELOCK_TIMEOUT_MINUTES = 120
    }

    private lateinit var wakeLock: PowerManager.WakeLock // Wake lock för att hålla skärmen på
    private lateinit var vibrator: Vibrator // Vibrator för att skapa vibrationseffekter

    // Initierar ViewModel genom delegation
    private val scoreViewModel: ScoreViewModel by viewModels()

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
        // Aktiverar wake lock för en bestämd tid
        wakeLock.acquire(DEFAULT_WAKELOCK_TIMEOUT_MINUTES * 60 * 1000L)

        // Sätter innehållet för aktivitetsvyn och startar WearApp UI-komponenten
        setContent {
            WearApp(
                vibrator = vibrator,
                appName = "Padel Score",
                viewModel = scoreViewModel
            )
        }
    }

    /**
     * Hanterar wakeLock när appen är pausad.
     * Detta sparar batteri när appen inte är aktiv.
     */
    override fun onPause() {
        super.onPause()
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }

    /**
     * Återaktiverar wakeLock när appen återupptas.
     */
    override fun onResume() {
        super.onResume()
        if (!wakeLock.isHeld) {
            wakeLock.acquire(DEFAULT_WAKELOCK_TIMEOUT_MINUTES * 60 * 1000L)
        }
    }

    /**
     * Frigör wake lock när aktiviteten förstörs för att undvika att hålla skärmen tänd i onödan.
     */
    override fun onDestroy() {
        super.onDestroy()
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }
}