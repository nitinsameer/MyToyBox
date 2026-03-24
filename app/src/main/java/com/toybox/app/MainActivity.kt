package com.toybox.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.toybox.app.screens.*
import com.toybox.app.ui.theme.ToyBoxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val themeVm: ThemeViewModel = viewModel()
            val isDark by themeVm.isDarkMode.collectAsState()
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.isAppearanceLightStatusBars  = !isDark
            controller.isAppearanceLightNavigationBars = !isDark
            ToyBoxTheme(isDark = isDark) {
                ToyBoxApp(themeVm = themeVm)
            }
        }
    }
}

@Composable
fun ToyBoxApp(themeVm: ThemeViewModel) {
    val navController = rememberNavController()
    val vm: ToyViewModel = viewModel()
    val userVm: UserViewModel = viewModel()
    val gamVm: GamificationViewModel = viewModel()

    val toys                 by vm.toys.collectAsState()
    val isDark               by themeVm.isDarkMode.collectAsState()
    val kidName              by userVm.kidName.collectAsState()
    val gamState             by gamVm.state.collectAsState()
    val newBadge             by gamVm.newBadge.collectAsState()
    val surprise             by gamVm.surprise.collectAsState()
    val completedChallenge   by gamVm.completedChallenge.collectAsState()
    val challengeProgresses  by gamVm.challengeProgresses.collectAsState()

    // Wait for DataStore before rendering NavHost
    if (kidName == null) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        return
    }

    val startDestination = if (kidName!!.isEmpty()) "setup" else "home"
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                if (currentRoute != "setup") BottomNav(navController)
            }
        ) { padding ->
            NavHost(
                navController    = navController,
                startDestination = startDestination,
                modifier         = Modifier.padding(padding)
            ) {
                composable("setup") {
                    SetupScreen(
                        userVm    = userVm,
                        onNameSaved = {
                            navController.navigate("home") {
                                popUpTo("setup") { inclusive = true }
                            }
                        }
                    )
                }
                composable("home") {
                    HomeScreen(
                        toys                = toys,
                        nav                 = navController,
                        themeVm             = themeVm,
                        isDark              = isDark,
                        kidName             = kidName ?: "",
                        gamState            = gamState,
                        challengeProgresses = challengeProgresses
                    )
                }
                composable("mytoys") {
                    MyToysScreen(
                        toys   = toys,
                        vm     = vm,
                        nav    = navController,
                        isDark = isDark
                    )
                }
                composable("addtoy") {
                    AddToyScreen(
                        vm              = vm,
                        nav             = navController,
                        isDark          = isDark,
                        gamVm           = gamVm,
                        currentToyCount = toys.size
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        userVm = userVm,
                        toyVm  = vm,
                        gamVm  = gamVm,
                        nav    = navController
                    )
                }
                composable("rewards") {
                    RewardsScreen(
                        gamVm = gamVm,
                        nav   = navController
                    )
                }
                composable("edittoy/{toyId}") { backStackEntry ->
                    val id = backStackEntry.arguments
                        ?.getString("toyId")
                        ?.toIntOrNull() ?: return@composable
                    EditToyScreen(
                        toyId  = id,
                        vm     = vm,
                        nav    = navController,
                        isDark = isDark
                    )
                }
            }
        }

        // ── Overlay popups (rendered above Scaffold) ──────────────────────────

        // Badge earned
        newBadge?.let { badge ->
            BadgeEarnedPopup(
                badge     = badge,
                onDismiss = { gamVm.dismissBadge() }
            )
        }

        // Surprise reward (20% chance after adding a toy)
        surprise?.let { reward ->
            SurprisePopup(
                reward    = reward,
                onDismiss = { gamVm.dismissSurprise() }
            )
        }

        // Challenge completed (inline card animation handles visual,
        // this snackbar-style banner adds an extra delight layer)
        completedChallenge?.let { progress ->
            ChallengeCompletedBanner(
                progress  = progress,
                onDismiss = { gamVm.dismissChallenge() }
            )
        }
    }
}
