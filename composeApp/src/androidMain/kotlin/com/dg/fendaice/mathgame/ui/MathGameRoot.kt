package com.dg.fendaice.mathgame.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dg.fendaice.mathgame.MathGameViewModel
import androidx.compose.foundation.layout.Column
import com.dg.fendaice.ui.components.BannerAdView
import com.dg.fendaice.R

@Composable
fun MathGameRoot(
    userName: String = "Player",
    viewModel: MathGameViewModel = viewModel(),
    initialDestination: GameDestination = GameDestination.Menu,
    onLogout: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf(initialDestination) }
    var bottomNavVisible by remember { mutableStateOf(true) }

    // Update screen if initialDestination changes (e.g. new intent)
    LaunchedEffect(initialDestination) {
        currentScreen = initialDestination
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (bottomNavVisible) {
                Column {
                    BannerAdView()
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == GameDestination.Menu,
                            onClick = { currentScreen = GameDestination.Menu },
                            icon = { Icon(Icons.Rounded.Home, null) },
                            label = { Text(stringResource(R.string.home_tab)) }
                        )
                        NavigationBarItem(
                            selected = currentScreen == GameDestination.Ranking,
                            onClick = { currentScreen = GameDestination.Ranking },
                            icon = { Icon(Icons.Rounded.Stars, null) },
                            label = { Text(stringResource(R.string.ranking_tab)) }
                        )
                        NavigationBarItem(
                            selected = currentScreen == GameDestination.Profile,
                            onClick = { currentScreen = GameDestination.Profile },
                            icon = { Icon(Icons.Rounded.Person, null) },
                            label = { Text(stringResource(R.string.profile_tab)) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val screen = currentScreen) {
                GameDestination.Menu -> {
                    bottomNavVisible = true
                    MenuScreen(
                        onCategorySelected = { ageGroup, topic ->
                            viewModel.startGame(ageGroup, topic, 1)
                            currentScreen = GameDestination.Game
                        }
                    )
                }
                GameDestination.Ranking -> {
                    bottomNavVisible = true
                    RankingScreen(viewModel = viewModel)
                }
                GameDestination.Profile -> {
                    bottomNavVisible = true
                    ProfileScreen(
                        userName = userName,
                        viewModel = viewModel,
                        onLogout = onLogout
                    )
                }
                GameDestination.Game -> {
                    bottomNavVisible = false
                    GameScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = GameDestination.Menu }
                    )
                }
            }
        }
    }
}

sealed class GameDestination {
    object Menu : GameDestination()
    object Ranking : GameDestination()
    object Profile : GameDestination()
    object Game : GameDestination()
}
