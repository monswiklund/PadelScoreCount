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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun ScoreCounter(vibrator: Vibrator, appName: String) {
    // Skapa och kom ihåg poäng och räknare för båda spelarna samt gems.
    var score1 by remember { mutableStateOf(0) }
    var score2 by remember { mutableStateOf(0) }
    var resetCount1 by remember { mutableStateOf(0) }
    var resetCount2 by remember { mutableStateOf(0) }
    var gemScore1 by remember { mutableStateOf(0) }
    var gemScore2 by remember { mutableStateOf(0) }
    val totalScore = score1 + score2

    // Funktion för att uppdatera gemScore1 om vissa villkor uppfylls.
    fun checkAndIncreaseGemScore1() {
        if (resetCount1 > 5 && resetCount1 >= resetCount2 + 2) {
            gemScore1 += 1
            resetCount1 = 0
            resetCount2 = 0
        }
    }

    // Funktion för att uppdatera gemScore2 om vissa villkor uppfylls.
    fun checkAndIncreaseGemScore2() {
        if (resetCount2 > 5 && resetCount2 >= resetCount1 + 2) {
            gemScore2 += 1
            resetCount1 = 0
            resetCount2 = 0
        }
    }

    // Kontrollera om en vit prick ska visas baserat på totala poängen.
    val shouldShowWhiteDot = totalScore == 0 || totalScore == 30 || totalScore == 55 || totalScore == 60 || totalScore == 80

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Bakgrundsfärg för appen
    ) {
        // Titel på appen visas högst upp i mitten
        Text(
            text = appName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
        )

        // Visa en vit prick i övre vänstra hörnet om 'shouldShowWhiteDot' är sant
        if (shouldShowWhiteDot) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White)
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            )
        }

        // Rad för huvudlayouten, innehåller återställningsknappen och spelarknappar
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Återställningsknapp som återställer poängen och räknarna
            Button(
                onClick = {
                    if (score1 == 0 && score2 == 0) {
                        resetCount1 = 0
                        resetCount2 = 0
                        gemScore1 = 0
                        gemScore2 = 0
                    } else {
                        score1 = 0
                        score2 = 0
                    }
                },
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(40.dp)
                    .background(brush = greyGradient),
                shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Text(text = "R", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(20.dp)) // Mellanslag mellan knapparna

            // Kolumn för att hålla båda spelarnas knappar
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Knapp för spelare 1 som ökar deras poäng
                Button(
                    onClick = {
                        score1 += 15
                        if (score1 == 45) score1 = 40 // Hantering för att hålla poäng vid 40
                        if (score1 >= 55) { // Om poängen når en viss nivå, återställ och öka resetCount
                            score1 = 0
                            score2 = 0
                            resetCount1 += 1
                            checkAndIncreaseGemScore1()
                        }
                        // Vibrerar vid knapptryck
                        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(brush = redGradient),
                    shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 4.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            ) {
                                append("Fiende $gemScore1/3\n\n")
                            }
                            withStyle(style = SpanStyle(fontSize = 50.sp, color = Color.White)) {
                                append("$score1:$resetCount1")
                            }
                        },
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 1.dp, top = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp)) // Mellanslag mellan knapparna

                // Knapp för spelare 2 som ökar deras poäng
                Button(
                    onClick = {
                        score2 += 15
                        if (score2 == 45) score2 = 40 // Hantering för att hålla poäng vid 40
                        if (score2 >= 55) { // Om poängen når en viss nivå, återställ och öka resetCount
                            score1 = 0
                            score2 = 0
                            resetCount2 += 1
                            checkAndIncreaseGemScore2()
                        }
                        // Vibrerar vid knapptryck
                        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(brush = blueGradient),
                    shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 0.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 50.sp, color = Color.White)) {
                                append("$score2:$resetCount2\n")
                            }
                            withStyle(style = SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)) {
                                append("Vi $gemScore2/3")
                            }
                        },
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 1.dp, bottom = 16.dp)
                    )
                }
            }
        }
    }
}