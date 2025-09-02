package com.example.padel3.presentation

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.padel3.presentation.theme.greyGradient
import com.example.padel3.presentation.theme.redGradient

@Composable
fun ScoreCounter(vibrator: Vibrator, appName: String, viewModel: ScoreViewModel) {
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
                    fontWeight = FontWeight.Bold,
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
                        Text("Fiende", color = Color.White, fontSize = 18.sp)
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
                        Text("Vi", color = Color.White, fontSize = 18.sp)
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
                Button(
                    onClick = {
                        if (viewModel.playerOneScore.value == 0 && viewModel.playerTwoScore.value == 0) {
                            // Om alla poäng är 0, visa mode-väljaren igen
                            viewModel.resetMatch()
                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            viewModel.resetGameScores()
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(brush = greyGradient),
                    shape = RoundedCornerShape(size=50.dp),
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Text(text = "R", fontSize = 20.sp)
                }

                Button(
                    onClick = {
                        if (viewModel.undo()) {
                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                        }
                    },
                    enabled = viewModel.canUndo.value,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = if (viewModel.canUndo.value) greyGradient else Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1F1F1F),
                                    Color(0xFF2A2A2A),
                                    Color(0xFF3C3C3C)
                                )
                            )
                        ),
                    shape = RoundedCornerShape(size=50.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        disabledBackgroundColor = Color.Transparent
                    )
                ) {
                    Text(
                        text = "⎌",
                        fontSize = 26.sp,
                        color = if (viewModel.canUndo.value) Color.White else Color(0xFF8A8A8A)
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
                            .fillMaxSize(),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                when (viewModel.gameMode.value) {
                                    GameMode.VINNARBANA -> {
                                        withStyle(
                                            style = SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        ) {
                                            append("Fiende ${viewModel.playerOneSetsWon.value}/3\n\n")
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
                                        withStyle(
                                            style = SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        ) {
                                            append("Fiende\n\n")
                                        }
                                        
                                        // Visa poäng för Mexicano
                                        withStyle(style = SpanStyle(fontSize = 60.sp, color = Color.White)) {
                                            append("${viewModel.playerOneScore.value}")
                                        }
                                        
                                        withStyle(style = SpanStyle(fontSize = 16.sp, color = Color.White)) {
                                            append("/${viewModel.mexicanoMatchLimit.value}")
                                        }
                                    }
                                }
                            },
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 1.dp, top = 16.dp)
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
                            .fillMaxSize(),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
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
                                        }

                                        withStyle(style = SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)) {
                                            append("Vi ${viewModel.playerTwoSetsWon.value}/3")
                                        }
                                    }
                                    GameMode.MEXICANO -> {
                                        // Visa poäng för Mexicano
                                        withStyle(style = SpanStyle(fontSize = 60.sp, color = Color.White)) {
                                            append("${viewModel.playerTwoScore.value}")
                                        }
                                        
                                        withStyle(style = SpanStyle(fontSize = 16.sp, color = Color.White)) {
                                            append("/${viewModel.mexicanoMatchLimit.value}\n")
                                        }

                                        withStyle(style = SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)) {
                                            append("Vi")
                                        }
                                    }
                                }
                            },
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 1.dp, bottom = 16.dp)
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
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                
                // Visa mode-indikator i nedre högra hörnet
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (viewModel.gameMode.value) {
                            GameMode.VINNARBANA -> "Vinnarbana"
                            GameMode.MEXICANO -> "Mexicano"
                        },
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
