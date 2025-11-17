package com.example.mediacontrol.Routes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mediacontrol.kotlin.mainClasses.ConfigureFloatingButton
import com.example.mediacontrol.kotlin.mainClasses.ManageFloatingButton

@Composable
fun MyAppNavigation() {
    val navController = rememberNavController();
    NavHost(
        navController = navController,
        startDestination = Routes.manageFloatingButton, builder = {
            composable(Routes.manageFloatingButton) {
                ManageFloatingButton(
                    navController = navController
                )
            }

            composable(Routes.configureFLoatingButton + "/{layoutId}/{key}") { backStackEntry ->
                // Get argument as String and parse to Int
                val layoutId = backStackEntry.arguments?.getString("layoutId")
                val key = backStackEntry.arguments?.getString("key")
                ConfigureFloatingButton(
                    navController = navController,
                    layoutId = layoutId,
                    sizePrefButtonName = key
                )
            }


        }

    )
}