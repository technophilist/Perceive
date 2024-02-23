package com.example.perceive.ui.navigation

sealed class PerceiveNavigationDestinations(val route: String) {
    data object WelcomeScreen : PerceiveNavigationDestinations("welcome_screen")
    data object HomeScreen : PerceiveNavigationDestinations("home_screen")

    data object ChatScreen : PerceiveNavigationDestinations("chat_screen/{initial_user_prompt}") {
        const val NAV_ARG_INITIAL_USER_PROMPT = "initial_user_prompt"
        fun buildRoute(initialUserPrompt: String) = "chat_screen/$initialUserPrompt"
    }
}