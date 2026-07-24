package com.dg.fendaice.mathgame.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dg.fendaice.R
import com.dg.fendaice.mathgame.MathGameViewModel
import com.dg.fendaice.mathgame.data.local.RankingUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(viewModel: MathGameViewModel) {
    val rankings by viewModel.globalRankings.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    val remainingRefreshes by viewModel.remainingRefreshes.collectAsState()

    // Automatically update rankings every time user enters ranking screen
    LaunchedEffect(Unit) {
        if (remainingRefreshes > 0) {
            viewModel.fetchRankings(isManual = true)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                title = { 
                    Text(
                        stringResource(R.string.ranking_title),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = stringResource(R.string.refreshes_left, remainingRefreshes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    RankingHeader()
                }
                items(rankings) { user ->
                    RankingItem(user, isCurrentUser = user.userId == currentUserId)
                }
            }
        }
    }
}

@Composable
fun RankingHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.rank_label), modifier = Modifier.weight(0.25f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(stringResource(R.string.name_label), modifier = Modifier.weight(0.45f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(stringResource(R.string.score_label), modifier = Modifier.weight(0.3f), fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
fun RankingItem(user: RankingUser, isCurrentUser: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Box(
                modifier = Modifier.weight(0.25f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (user.rank <= 3) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = when (user.rank) {
                            1 -> Color(0xFFFFD700) // Gold
                            2 -> Color(0xFFC0C0C0) // Silver
                            else -> Color(0xFFCD7F32) // Bronze
                        },
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${user.rank}",
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Name
            Text(
                text = user.userName,
                modifier = Modifier.weight(0.45f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimaryContainer else Color.Unspecified
            )

            // Score
            Text(
                text = "${user.totalScore}",
                modifier = Modifier.weight(0.3f),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
    }
}
