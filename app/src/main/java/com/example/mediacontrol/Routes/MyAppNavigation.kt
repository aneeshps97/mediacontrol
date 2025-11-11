package com.example.mediacontrol.Routes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
        }
    )
}