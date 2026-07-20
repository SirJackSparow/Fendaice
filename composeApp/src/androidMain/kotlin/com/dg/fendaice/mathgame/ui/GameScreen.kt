package com.dg.fendaice.mathgame.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.dg.fendaice.mathgame.MathGameViewModel
import com.dg.fendaice.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(viewModel: MathGameViewModel, onBack: () -> Unit) {
    val currentQuestion by viewModel.currentQuestion.collectAsState()
    val score by viewModel.score.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val totalQuestions by viewModel.questions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isComplete by viewModel.isComplete.collectAsState()
    val lastAnswerCorrect by viewModel.lastAnswerCorrect.collectAsState()

    if (isLoading) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background), 
            contentAlignment = Alignment.Center
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.Url("https://lottie.host/8c0938aa-5e57-4498-8f7b-6d6eb713972c/eXsV4UNukX.json"))
            LottieAnimation(composition, iterations = LottieConstants.IterateForever, modifier = Modifier.size(250.dp))
        }
    } else if (isComplete) {
        ScoreScreen(
            score = score,
            total = totalQuestions.size,
            onRestart = { viewModel.restartGame() },
            onExit = onBack
        )
    } else {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val screenWidth = maxWidth
            val screenHeight = maxHeight

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.LightGray.copy(alpha = 0.1f), 
                    radius = 400f, 
                    center = Offset(size.width * 0.1f, size.height * 0.2f)
                )
            }

            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        title = { 
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "MATH CHALLENGE", 
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                                )
                                Text(
                                    "${currentIndex + 1} / ${totalQuestions.size}", 
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically, 
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Star, 
                                        null, 
                                        tint = Color(0xFFFFD700), 
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    AnimatedContent(
                                        targetState = score,
                                        transitionSpec = {
                                            if (targetState > initialState) {
                                                slideInVertically { height -> height } + fadeIn() togetherWith
                                                        slideOutVertically { height -> -height } + fadeOut()
                                            } else {
                                                slideInVertically { height -> -height } + fadeIn() togetherWith
                                                        slideOutVertically { height -> height } + fadeOut()
                                            }.using(
                                                SizeTransform(clip = false)
                                            )
                                        }
                                    ) { targetScore ->
                                        Text(
                                            "$targetScore", 
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    currentQuestion?.let { question ->
                        // Responsive Question Display
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Card(
                                modifier = Modifier.fillMaxWidth().height(screenHeight * 0.25f),
                                shape = RoundedCornerShape(32.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    val parts = question.questionText.split("?")
                                    Text(
                                        parts.getOrElse(0) { "" }, 
                                        fontSize = 44.sp, 
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    
                                    val bgColor by animateColorAsState(
                                        targetValue = when (lastAnswerCorrect) {
                                            true -> Color(0xFF4CAF50)
                                            false -> Color(0xFFF44336)
                                            else -> MaterialTheme.colorScheme.primaryContainer
                                        },
                                        animationSpec = tween(300)
                                    )

                                    val scale by animateFloatAsState(
                                        targetValue = if (lastAnswerCorrect != null) 1.2f else 1.0f,
                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 12.dp)
                                            .size(70.dp, 70.dp)
                                            .graphicsLayer(scaleX = scale, scaleY = scale)
                                            .background(bgColor, RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (lastAnswerCorrect == true) question.correctAnswer else "?",
                                            fontSize = 28.sp, 
                                            fontWeight = FontWeight.ExtraBold, 
                                            color = if (lastAnswerCorrect != null) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                    
                                    Text(
                                        parts.getOrElse(1) { "" }, 
                                        fontSize = 44.sp, 
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Correct Answer Lottie Overlay (Verified URL)
                            if (lastAnswerCorrect == true) {
                                val composition by rememberLottieComposition(LottieCompositionSpec.Url("https://lottie.host/79ed564b-7e14-4a8f-980f-3f592addf735/XmjPS9Ja2t.json"))
                                LottieAnimation(composition, iterations = 1, modifier = Modifier.size(screenWidth * 0.4f))
                            }
                        }

                        // Responsive Answer Options
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(R.string.tap_correct_answer), 
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )
                            
                            val rows = question.answerTiles.chunked(2)
                            rows.forEach { rowTiles ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    rowTiles.forEach { tileText ->
                                        AnswerTile(
                                            text = tileText,
                                            width = screenWidth * 0.4f,
                                            height = screenHeight * 0.1f,
                                            onClick = { viewModel.submitAnswer(tileText) }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerTile(text: String, width: androidx.compose.ui.unit.Dp, height: androidx.compose.ui.unit.Dp, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(width, height)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text, 
                fontSize = 28.sp, 
                fontWeight = FontWeight.ExtraBold, 
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
