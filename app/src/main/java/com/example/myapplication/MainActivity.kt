package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.theme.MyApplicationTheme

// Screen enum for navigation
enum class AppScreen {
    WELCOME,
    TERMINAL,
    ACHIEVEMENTS,
    SKILLS,
    EQUIPMENT,
    MISSIONS,
    SHOP
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentScreen = remember { mutableStateOf(AppScreen.WELCOME) }
                    val viewModel = remember { TerminalViewModel() }
                    
                    when (currentScreen.value) {
                        AppScreen.WELCOME -> {
                            WelcomeScreen(
                                onGetStarted = { currentScreen.value = AppScreen.TERMINAL }
                            )
                        }
                        AppScreen.TERMINAL -> {
                            TerminalScreen(
                                viewModel = viewModel,
                                onNavigateToAchievements = { currentScreen.value = AppScreen.ACHIEVEMENTS },
                                onNavigateToSkills = { currentScreen.value = AppScreen.SKILLS },
                                onNavigateToEquipment = { currentScreen.value = AppScreen.EQUIPMENT },
                                onNavigateToMissions = { currentScreen.value = AppScreen.MISSIONS },
                                onNavigateToShop = { currentScreen.value = AppScreen.SHOP }
                            )
                        }
                        AppScreen.ACHIEVEMENTS -> {
                            AchievementsScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen.value = AppScreen.TERMINAL }
                            )
                        }
                        AppScreen.SKILLS -> {
                            SkillTreeScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen.value = AppScreen.TERMINAL }
                            )
                        }
                        AppScreen.EQUIPMENT -> {
                            EquipmentScreen(
                                viewModel = viewModel,
                                equipmentSystem = EquipmentSystem(),
                                onBack = { currentScreen.value = AppScreen.TERMINAL },
                                onNavigateToShop = { currentScreen.value = AppScreen.SHOP }
                            )
                        }
                        AppScreen.MISSIONS -> {
                            MissionsScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen.value = AppScreen.TERMINAL }
                            )
                        }
                        AppScreen.SHOP -> {
                            ShopScreen(
                                viewModel = viewModel,
                                equipmentSystem = EquipmentSystem(),
                                onBack = { 
                                    // Go back to equipment screen if we came from there
                                    if (currentScreen.value == AppScreen.EQUIPMENT) {
                                        currentScreen.value = AppScreen.EQUIPMENT
                                    } else {
                                        currentScreen.value = AppScreen.TERMINAL
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}