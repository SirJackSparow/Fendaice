package com.dg.fendaice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dg.fendaice.mathgame.MathGameViewModel
import com.dg.fendaice.mathgame.ui.MathGameRoot
import com.dg.fendaice.mathgame.ui.RegistrationScreen
import com.dg.fendaice.ui.theme.FendaiceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            FendaiceTheme {
                val gameViewModel = viewModel<MathGameViewModel>()
                val userStats by gameViewModel.userStats.collectAsState()

                val userName = userStats?.userName

                if (!userName.isNullOrBlank()) {
                    MathGameRoot(
                        userName = userName,
                        viewModel = gameViewModel,
                        onLogout = {
                            // For local mode, "Logout" can mean clearing the name to "re-register"
                            gameViewModel.registerUser("") 
                        }
                    )
                } else {
                    RegistrationScreen(
                        onRegister = { newName ->
                            gameViewModel.registerUser(newName)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
