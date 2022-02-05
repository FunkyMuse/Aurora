package com.funkymuse.aurora.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.funkymuse.aurora.navigator.NavigationDestination
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet


private val bottomSheetDestinations: Map<NavigationDestination, @Composable (NavBackStackEntry, NavHostController) -> Unit>
    get() = mapOf(

    )

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.addBottomSheetDestinations(navController: NavHostController) {
    bottomSheetDestinations.forEach { entry ->
        val destination = entry.key
        bottomSheet(destination.route(), destination.arguments, destination.deepLinks) { navEntry ->
            entry.value(navEntry, navController)
        }
    }
}
