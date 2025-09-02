package com.example.padel3.presentation
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScoreCounter(vibrator: Vibrator, appName: String, viewModel: ScoreViewModel) {

class ScoreViewModel : ViewModel() {
    val showModeSelector = mutableStateOf(false)}
    // ------------------------
    // Helper för vibration
    // ------------------------
    fun vibrateShort() = vibrator.vibrate(
        VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
    )

    // ------------------------
    // Track previous scores för animation
    // ------------------------
    var previousPlayerOneScore by remember { mutableStateOf(viewModel.playerOneScore.value) }
    var previousPlayerTwoScore by remember { mutableStateOf(viewModel.playerTwoScore.value) }
    var playerOneScaleTrigger by remember { mutableStateOf(1f) }
    var playerTwoScaleTrigger by remember { mutableStateOf(1f) }

    LaunchedEffect(viewModel.playerOneScore.value) {
        if (previousPlayerOneScore != viewModel.playerOneScore.value) {
            previousPlayerOneScore = viewModel.playerOneScore.value
            playerOneScaleTrigger = 1.03f
            kotlinx.coroutines.delay(100)
            playerOneScaleTrigger = 1f
        }
    }
    LaunchedEffect(viewModel.playerTwoScore.value) {
        if (previousPlayerTwoScore != viewModel.playerTwoScore.value) {
            previousPlayerTwoScore = viewModel.playerTwoScore.value
            playerTwoScaleTrigger = 1.03f
            kotlinx.coroutines.delay(100)
            playerTwoScaleTrigger = 1f
        }
    }

    val playerOneScale by animateFloatAsState(playerOneScaleTrigger, spring(0.8f, 600f))
    val playerTwoScale by animateFloatAsState(playerTwoScaleTrigger, spring(0.8f, 600f))

    // ------------------------
    // Visa selectors först
    // ------------------------
    if (viewModel.showModeSelector.value) { ModeSelector(vibrator, viewModel); return }
    if (viewModel.showServeSelector.value) {
        Box(
            Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Button(
                    onClick = { viewModel.setInitialServer(true); vibrateShort() },
                    modifier = Modifier.size(80.dp)
                        .background(Color(0xFFFF4500), RoundedCornerShape(10)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                ) {}
                Button(
                    onClick = { viewModel.setInitialServer(false); vibrateShort() },
                    modifier = Modifier.size(80.dp)
                        .background(Color(0xFF3372E0), RoundedCornerShape(10)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                ) {}
            }
        }
        return
    }

    // ------------------------
    // ROOT BOX: spelplan + overlay
    // ------------------------
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // Spelplan
        Column(modifier = Modifier.fillMaxSize()) {
            // Player One
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFFFF4500), Color(0xFFFF8C00))))
            ) {
                Button(
                    onClick = { viewModel.incrementPlayerOneScore() },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        "${viewModel.playerOneScore.value}",
                        color = Color.White,
                        fontSize = 50.sp,
                        modifier = Modifier.graphicsLayer(scaleX = playerOneScale, scaleY = playerOneScale)
                    )
                }
            }

            // Player Two
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF3372E0), Color(0xFF69B3FF))))
            ) {
                Button(
                    onClick = { viewModel.incrementPlayerTwoScore() },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        "${viewModel.playerTwoScore.value}",
                        color = Color.White,
                        fontSize = 50.sp,
                        modifier = Modifier.graphicsLayer(scaleX = playerTwoScale, scaleY = playerTwoScale)
                    )
                }
            }
        }

        // Overlay: Reset & Undo
        Column(
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassButton("↻", viewModel, vibrator) { viewModel.resetGameScores() }
            GlassButton("↶", viewModel, vibrator) { if (viewModel.undo()) vibrateShort() }
        }

        // Serve-indikator för VINNARBANA tiebreak
        if (viewModel.gameMode.value == GameMode.VINNARBANA && viewModel.isTieBreak.value) {
            val serveColor = if (viewModel.isPlayerOneServing.value) Color(0xFFFF4500) else Color(0xFF3372E0)
            Box(
                modifier = Modifier.size(40.dp)
                    .background(serveColor.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                    .align(if (viewModel.isPlayerOneServing.value) Alignment.CenterStart else Alignment.CenterEnd),
                contentAlignment = Alignment.Center
            ) { Text("S", color = Color.White) }
        }

        // Total poäng för Mexicano
        if (viewModel.gameMode.value == GameMode.MEXICANO) {
            val totalPoints = viewModel.playerOneScore.value + viewModel.playerTwoScore.value
            var previousTotal by remember { mutableStateOf(totalPoints) }
            var totalScaleTrigger by remember { mutableStateOf(1f) }
            LaunchedEffect(totalPoints) {
                if (previousTotal != totalPoints) {
                    previousTotal = totalPoints
                    totalScaleTrigger = 1.05f
                    kotlinx.coroutines.delay(80)
                    totalScaleTrigger = 1f
                }
            }
            val totalScale by animateFloatAsState(totalScaleTrigger, spring(0.7f, 800f))
            Box(
                modifier = Modifier.align(Alignment.Center)
                    .graphicsLayer(scaleX = totalScale, scaleY = totalScale)
                    .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) { Text("Total: $totalPoints/${viewModel.mexicanoMatchLimit.value}", color = Color.Black) }
        }
    }
}
@Composable
fun GlassButton(
    label: String,
    viewModel: ScoreViewModel,
    vibrator: Vibrator,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var pressStartTime by remember { mutableStateOf(0L) }  // korrekt state

    LaunchedEffect(isPressed) {
        if (isPressed) {
            pressStartTime = System.currentTimeMillis()
        } else {
            val pressDuration = System.currentTimeMillis() - pressStartTime
            if (pressDuration < 500L) {
                onClick()
            } else {
                viewModel.showModeSelector.value = true
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }

    Button(
        onClick = {}, // hanteras via LaunchedEffect
        interactionSource = interactionSource,
        modifier = Modifier.size(50.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        shape = RoundedCornerShape(50)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.2f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.1f)
                        )
                    ),
                    RoundedCornerShape(50)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(label, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}