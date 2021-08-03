package com.funkymuse.aurora.bottomnavigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.funkymuse.style.shape.BottomSheetShapes
import com.google.accompanist.insets.navigationBarsPadding

/**
 * Created by funkymuse on 6/27/21 to long live and prosper !
 */


@Composable
fun AuroraBottomNavigation(navController: NavHostController, bottomNavList: List<BottomEntry>) {

    var hideBottomNav by rememberSaveable { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val size = if (hideBottomNav) {
        Modifier.size(animateDpAsState(targetValue = 0.dp, animationSpec = tween()).value)
    } else {
        Modifier
    }

    BottomNavigation(
        modifier = size
            .clip(BottomSheetShapes.large)
            .navigationBarsPadding()
    ) {
        hideBottomNav = currentRoute in BottomNav.hideBottomNavOnDestinations
        bottomNavList.forEach { bottomEntry ->
            BottomNavigationItem(
                selected = currentRoute == bottomEntry.screen.route,
                alwaysShowLabel = false,
                onClick = {
                    navController.navigate(bottomEntry.screen.route) {
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                },
                label = { Text(text = stringResource(id = bottomEntry.screen.resourceID)) },
                icon = {
                    Icon(
                        imageVector = bottomEntry.icon,
                        contentDescription = stringResource(id = bottomEntry.screen.resourceID)
                    )
                }
            )
        }
    }
}

