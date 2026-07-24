package com.dg.fendaice

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dg.fendaice.mathgame.MathGameViewModel
import com.dg.fendaice.mathgame.ui.AuthScreen
import com.dg.fendaice.mathgame.ui.GameDestination
import com.dg.fendaice.mathgame.ui.MathGameRoot
import com.dg.fendaice.ui.theme.FendaiceTheme
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize Google Mobile Ads (AdMob)
        MobileAds.initialize(this) {}

        setContent {
            FendaiceTheme {
                RequestNotificationPermission()

                val gameViewModel = viewModel<MathGameViewModel>()
                val userStats by gameViewModel.userStats.collectAsState()
                var loginError by remember { mutableStateOf(false) }

                val userName = userStats?.userName

                if (!userName.isNullOrBlank()) {
                    val notificationType = intent.getStringExtra("notification_type")
                    val destination = when (notificationType) {
                        "ranking" -> GameDestination.Ranking
                        "challenge" -> GameDestination.Menu
                        else -> GameDestination.Menu
                    }

                    MathGameRoot(
                        userName = userName,
                        viewModel = gameViewModel,
                        initialDestination = destination,
                        onLogout = {
                            gameViewModel.logout()
                        }
                    )
                } else {
                    AuthScreen(
                        loginFailed = loginError,
                        onLogin = { name, password ->
                            gameViewModel.loginUser(name, password) { success ->
                                loginError = !success
                            }
                        },
                        onRegister = { name, password ->
                            gameViewModel.registerUser(name, password)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    val permission = Manifest.permission.POST_NOTIFICATIONS
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* user choice handled by system */ }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) {
            launcher.launch(permission)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {

}
