package com.example.padel3.presentation

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import com.example.padel3.presentation.theme.blueGradient
import com.example.padel3.presentation.theme.redGradient

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScoreCounter(vibrator: Vibrator, appName: String, viewModel: ScoreViewModel) {
    // Track previous scores to detect changes for animation
    var previousPlayerOneScore by remember { mutableStateOf(viewModel.playerOneScore.value) }
    var previousPlayerTwoScore by remember { mutableStateOf(viewModel.playerTwoScore.value) }
    
    // Animation triggers
    var playerOneScaleTrigger by remember { mutableStateOf(1f) }
    var playerTwoScaleTrigger by remember { mutableStateOf(1f) }
    
    // Detect score changes and trigger animations
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
    
    // Animation states
    val playerOneScale by animateFloatAsState(
        targetValue = playerOneScaleTrigger,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 600f),
        label = "playerOneScale"
    )
    
    val playerTwoScale by animateFloatAsState(
        targetValue = playerTwoScaleTrigger,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 600f),
        label = "playerTwoScale"
    )
    
    // Button press interaction sources
    val playerOneInteractionSource = remember { MutableInteractionSource() }
    val playerTwoInteractionSource = remember { MutableInteractionSource() }
    
    // Button press states
    val isPlayerOnePressed by playerOneInteractionSource.collectIsPressedAsState()
    val isPlayerTwoPressed by playerTwoInteractionSource.collectIsPressedAsState()
    
    // Press animations for shadow movement
    val playerOneElevation by animateFloatAsState(
        targetValue = if (isPlayerOnePressed) 3.dp.value else 4.dp.value,
        animationSpec = tween(durationMillis = 80),
        label = "playerOneElevation"
    )
    
    val playerTwoElevation by animateFloatAsState(
        targetValue = if (isPlayerTwoPressed) 3.dp.value else 4.dp.value,
        animationSpec = tween(durationMillis = 80),
        label = "playerTwoElevation"
    )
    // Visa mode-väljare först om den behövs
    if (viewModel.showModeSelector.value) {
        ModeSelector(vibrator = vibrator, viewModel = viewModel)
        return
    }
    
    // Visa endast serve-väljaren när den behövs, dölj all annan UI
    if (viewModel.showServeSelector.value) {
        // Full svart skärm med endast serve-väljare
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Vem servar först i tiebreak?",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Röd knapp (Fiende)
                    Button(
                        onClick = {
                            viewModel.setInitialServer(true)
                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                        },
                        modifier = Modifier
                            .size(80.dp)
                            .background(brush = redGradient, shape = RoundedCornerShape(percent = 10)),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                    ) {
                    }

                    // Blå knapp (Vi)
                    Button(
                        onClick = {
                            viewModel.setInitialServer(false)
                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                        },
                        modifier = Modifier
                            .size(80.dp)
                            .background(brush = blueGradient, shape = RoundedCornerShape(percent = 10)),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                    ) {
                    }
                }
            }
        }
        return // Återvänd tidigt för att inte visa resten av UI:t
    }

    // Resten av UI visas bara om serve-väljaren inte är aktiv
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Beräkna när vita fyrkanten ska visas
        val totalScore = viewModel.playerOneScore.value + viewModel.playerTwoScore.value
        val totalTieBreakPoints = viewModel.playerOneTieBreakPoints.value + viewModel.playerTwoTieBreakPoints.value

        val shouldShowWhiteDot = if (viewModel.isTieBreak.value) {
            // I tiebreak: visa vid udda poäng (serva från vänster sida)
            totalTieBreakPoints % 2 == 1
        } else {
            // Vanligt spel: behåll befintlig logik
            totalScore == 0 || totalScore == 30 ||
                    totalScore == 55 || totalScore == 60 ||
                    totalScore == 80
        }

        if (shouldShowWhiteDot) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White)
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .combinedClickable(
                            onClick = {
                                if (viewModel.playerOneScore.value == 0 && viewModel.playerTwoScore.value == 0) {
                                    // Om alla poäng är 0, visa mode-väljaren igen
                                    viewModel.resetMatch(showModeSelector = false)
                                } else {
                                    viewModel.resetGameScores()
                                }
                            },
                            onLongClick = {
                                // Long press: visa mode-väljaren oavsett poängställning
                                viewModel.resetMatch(showModeSelector = true)
                                vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Glass effect overlay
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.2f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.1f)
                                    )
                                ),
                                shape = RoundedCornerShape(size = 50.dp)
                            )
                    )
                    
                    Text(
                        text = "↻",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                                            )
                }

                Box(
                    modifier = Modifier
                        .combinedClickable(
                            onClick = {
                                if (viewModel.undo()) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                                }
                            },
                            enabled = viewModel.canUndo.value
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Glass effect overlay
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = if (viewModel.canUndo.value) listOf(
                                        Color.White.copy(alpha = 0.2f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.1f)
                                    ) else listOf(
                                        Color.White.copy(alpha = 0.05f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.05f)
                                    )
                                ),
                                shape = RoundedCornerShape(size = 50.dp)
                            )
                    )
                    
                    Text(
                        text = "↶",
                        fontSize = 18.sp,
                        color = if (viewModel.canUndo.value) Color.White else Color(0xFF8A8A8A),
                        fontWeight = FontWeight.Medium,
                                            )
                }
            }

            Spacer(modifier = Modifier.width(25.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .align(Alignment.TopCenter)
                        .background(brush = redGradient)
                ) {
                    Button(
                        onClick = {
                            viewModel.incrementPlayerOneScore()
                            // Dubbel vibration för den röda knappen (fiende)
                            val timings = longArrayOf(0, 100, 100, 100)
                            val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)
                            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                shadowElevation = playerOneElevation
                            ),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        interactionSource = playerOneInteractionSource
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                when (viewModel.gameMode.value) {
                                    GameMode.VINNARBANA -> {
                                        withStyle(
                                            style = SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                                        ) {
                                            append("${viewModel.playerOneSetsWon.value}/3 Sets Won\n\n")
                                        }

                                        if (viewModel.isTieBreak.value) {
                                            // Visa tiebreak-poäng
                                            withStyle(style = SpanStyle(fontSize = 50.sp, color = Color.White)) {
                                                append("${viewModel.playerOneTieBreakPoints.value}T")
                                            }
                                            withStyle(style = SpanStyle(fontSize = 25.sp, color = Color.White)) {
                                                append(":${viewModel.playerOneGamesWon.value}")
                                            }
                                        } else {
                                            // Vanlig poängvisning
                                            withStyle(style = SpanStyle(fontSize = 50.sp, color = Color.White)) {
                                                append("${viewModel.playerOneScore.value}:${viewModel.playerOneGamesWon.value}")
                                            }
                                        }
                                    }
                                    GameMode.MEXICANO -> {
                                        // Visa poäng för Mexicano
                                        withStyle(style = SpanStyle(fontSize = 60.sp, color = Color.White)) {
                                            append("${viewModel.playerOneScore.value}")
                                        }
                                    }
                                }
                            },
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 1.dp, top = 16.dp)
                                .graphicsLayer(scaleX = playerOneScale, scaleY = playerOneScale)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .align(Alignment.BottomCenter)
                        .background(brush = blueGradient)

                ) {
                    Button(
                        onClick = {
                            viewModel.incrementPlayerTwoScore()
                            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                shadowElevation = playerTwoElevation
                            ),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        interactionSource = playerTwoInteractionSource
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                when (viewModel.gameMode.value) {
                                    GameMode.VINNARBANA -> {
                                        if (viewModel.isTieBreak.value) {
                                            // Visa tiebreak-poäng
                                            withStyle(style = SpanStyle(fontSize = 50.sp, color = Color.White)) {
                                                append("${viewModel.playerTwoTieBreakPoints.value}T")
                                            }
                                            withStyle(style = SpanStyle(fontSize = 25.sp, color = Color.White)) {
                                                append(":${viewModel.playerTwoGamesWon.value}\n")
                                            }
                                        } else {
                                            // Vanlig poängvisning
                                            withStyle(style = SpanStyle(fontSize = 50.sp, color = Color.White)) {
                                                append("${viewModel.playerTwoScore.value}:${viewModel.playerTwoGamesWon.value}\n")
                                            }

                                        withStyle(
                                            style = SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                                        ) {
                                            append("${viewModel.playerTwoSetsWon.value}/3 Sets Won\n\n")
                                        }
                                        }
                                    }
                                    GameMode.MEXICANO -> {
                                        // Visa poäng för Mexicano
                                        withStyle(style = SpanStyle(fontSize = 60.sp, color = Color.White)) {
                                            append("${viewModel.playerTwoScore.value}")
                                        }
                                        }
                                    }
                            },
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 1.dp, bottom = 16.dp)
                                .graphicsLayer(scaleX = playerTwoScale, scaleY = playerTwoScale)
                        )
                    }
                }

                // Visa total poäng-räknare för Mexicano
                if (viewModel.gameMode.value == GameMode.MEXICANO) {
                    val totalPoints = viewModel.playerOneScore.value + viewModel.playerTwoScore.value
                    
                    // Track previous total for bounce animation
                    var previousTotal by remember { mutableStateOf(totalPoints) }
                    var totalScaleTrigger by remember { mutableStateOf(1f) }
                    
                    // Trigger scale bounce when total changes
                    LaunchedEffect(totalPoints) {
                        if (previousTotal != totalPoints) {
                            previousTotal = totalPoints
                            totalScaleTrigger = 1.05f
                            kotlinx.coroutines.delay(80)
                            totalScaleTrigger = 1f
                        }
                    }
                    
                    // Animation för fade-in effekt när poängen uppdateras
                    val alpha by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 300),
                        label = "totalPointsAlpha"
                    )
                    
                    // Scale bounce animation
                    val totalScale by animateFloatAsState(
                        targetValue = totalScaleTrigger,
                        animationSpec = spring(dampingRatio = 0.7f, stiffness = 800f),
                        label = "totalScale"
                    )
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .alpha(alpha)
                            .graphicsLayer(scaleX = totalScale, scaleY = totalScale)
                            .background(
                                Color.White.copy(alpha = 0.7f), 
                                shape = RoundedCornerShape(16.dp) // Pill-shaped
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Total: $totalPoints/${viewModel.mexicanoMatchLimit.value}",
                            fontSize = 12.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                                                    )
                    }
                }

                // Visa tiebreak-indikator och serve-indikator endast för Vinnarbana
                if (viewModel.gameMode.value == GameMode.VINNARBANA && viewModel.isTieBreak.value) {

                    // Serve-indikator - visa en liten färgad cirkel som indikerar vem som servar
                    val serveColor = if (viewModel.isPlayerOneServing.value) {
                        Color(0xFFFF4500) // Röd för fiende (spelare 1)
                    } else {
                        Color(0xFF3372E0) // Blå för vi (spelare 2)
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(serveColor, shape = RoundedCornerShape(20.dp))
                            .align(if (viewModel.isPlayerOneServing.value) Alignment.CenterStart else Alignment.CenterEnd)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "S",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                                                        modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
