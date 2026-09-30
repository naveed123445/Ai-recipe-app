package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.FoodAlchemistBottomNav
import com.example.ui.screens.CookingModeScreen
import com.example.ui.screens.FusionLabScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KitchenScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RecipeDetailScreen
import com.example.ui.screens.SavedRecipesScreen
import com.example.ui.screens.ScanIngredientsScreen
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FoodAlchemistViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: FoodAlchemistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FoodAlchemistApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FoodAlchemistApp(viewModel: FoodAlchemistViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle system back navigation across all secondary screens
    val isSecondaryScreen = currentScreen !is Screen.Home
    BackHandler(enabled = isSecondaryScreen) {
        viewModel.navigateBack()
    }

    val showBottomBar = currentScreen is Screen.Home ||
            currentScreen is Screen.FusionLab ||
            currentScreen is Screen.Kitchen ||
            currentScreen is Screen.Saved ||
            currentScreen is Screen.Profile

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CharcoalDark),
        containerColor = CharcoalDark,
        bottomBar = {
            if (showBottomBar) {
                FoodAlchemistBottomNav(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentScreen,
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    is Screen.Home -> HomeScreen(viewModel = viewModel)
                    is Screen.Scan -> ScanIngredientsScreen(viewModel = viewModel)
                    is Screen.FusionLab -> FusionLabScreen(viewModel = viewModel)
                    is Screen.RecipeDetail -> RecipeDetailScreen(
                        recipe = screen.recipe,
                        viewModel = viewModel
                    )
                    is Screen.CookingMode -> CookingModeScreen(
                        recipe = screen.recipe,
                        viewModel = viewModel
                    )
                    is Screen.Kitchen -> KitchenScreen(viewModel = viewModel)
                    is Screen.Saved -> SavedRecipesScreen(viewModel = viewModel)
                    is Screen.Profile -> ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }
}
