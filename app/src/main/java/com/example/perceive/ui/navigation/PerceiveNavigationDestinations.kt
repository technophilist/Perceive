package com.example.perceive.ui.navigation

sealed class PerceiveNavigationDestinations(val route: String) {
    data object WelcomeScreen : PerceiveNavigationDestinations("welcome_screen")
    data object HomeScreen : PerceiveNavigationDestinations("home_screen")
}