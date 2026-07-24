package com.dg.fendaice.mathgame.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.dg.fendaice.R
import com.dg.fendaice.ui.components.BannerAdView

@Composable
fun ScoreScreen(
    score: Int,
    total: Int,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    val stars = when {
        score >= total * 8 -> 3
        score >= total * 5 -> 2
        score >= total * 2 -> 1
        else -> 0
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Confetti Lottie if 3 stars (Verified URL)
        if (stars == 3) {
            val composition by rememberLottieComposition(LottieCompositionSpec.Url("https://lottie.host/79ed564b-7e14-4a8f-980f-3f592addf735/XmjPS9Ja2t.json"))
            LottieAnimation(
                composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (stars == 3) "Excellent!" else "Game Over!",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            
            Spacer(Modifier.height(24.dp))
            
            Row {
                repeat(3) { index ->
                    val isSelected = index < stars
                    Text(
                        text = "⭐",
                        fontSize = 56.sp,
                        color = if (isSelected) Color.Unspecified else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    "You scored $score points",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
            
            Spacer(Modifier.height(64.dp))
            
            Button(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(stringResource(R.string.try_again_button), style = MaterialTheme.typography.titleMedium)
            }
            
            Spacer(Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onExit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(stringResource(R.string.back_to_menu_button), style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(16.dp))
            BannerAdView()
        }
    }
}
