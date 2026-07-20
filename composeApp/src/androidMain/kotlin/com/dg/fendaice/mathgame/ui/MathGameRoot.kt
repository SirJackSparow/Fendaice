package com.dg.fendaice.mathgame.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dg.fendaice.mathgame.MathGameViewModel
import com.dg.fendaice.R

@Composable
fun MathGameRoot(
    userName: String = "Player",
    viewModel: MathGameViewModel = viewModel(),
    onLogout: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf<GameDestination>(GameDestination.Menu) }
    var bottomNavVisible by remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            if (bottomNavVisible) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentScreen == GameDestination.Menu,
                        onClick = { currentScreen = GameDestination.Menu },
                        icon = { Icon(Icons.Rounded.Home, null) },
                        label = { Text(stringResource(R.string.home_tab)) }
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
    object Profile : GameDestination()
    object Game : GameDestination()
}
