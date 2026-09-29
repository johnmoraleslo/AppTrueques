package com.johnmoraleslo.apptrueques.ui.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.johnmoraleslo.apptrueques.ui.screens.AddItemScreen
import com.johnmoraleslo.apptrueques.ui.screens.ExploreScreen
import com.johnmoraleslo.apptrueques.ui.screens.ItemDetailScreen
import com.johnmoraleslo.apptrueques.ui.screens.MyItemsScreen
import com.johnmoraleslo.apptrueques.ui.screens.OffersScreen
import com.johnmoraleslo.apptrueques.ui.viewmodel.TradeViewModel

object Routes {
    const val EXPLORE = "explore"
    const val MY_ITEMS = "my_items"
    const val OFFERS = "offers"
    const val ADD_ITEM = "add_item"
    const val DETAIL = "detail/{itemId}"

    fun detail(itemId: Int) = "detail/$itemId"
}

// Pestañas de la barra inferior
data class BottomTab(val route: String, val label: String, val icon: ImageVector)

val bottomTabs = listOf(
    BottomTab(Routes.EXPLORE, "Explorar", Icons.Default.Explore),
    BottomTab(Routes.MY_ITEMS, "Mis cosas", Icons.Default.Inventory2),
    BottomTab(Routes.OFFERS, "Trueques", Icons.Default.SwapHoriz)
)

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    // Un solo TradeViewModel para toda la app, así todas las pantallas
    // ven los mismos artículos y ofertas
    val tradeViewModel: TradeViewModel = viewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // La barra inferior solo se muestra en las pantallas principales
    val showBottomBar = bottomTabs.any { it.route == currentRoute }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(Routes.EXPLORE)
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.EXPLORE,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable(Routes.EXPLORE) {
                ExploreScreen(
                    onItemClick = { id -> navController.navigate(Routes.detail(id)) }
                )
            }

            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("itemId") { type = NavType.IntType })
            ) { entry ->
                val itemId = entry.arguments?.getInt("itemId") ?: 0
                ItemDetailScreen(
                    itemId = itemId,
                    tradeViewModel = tradeViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.MY_ITEMS) {
                MyItemsScreen(
                    tradeViewModel = tradeViewModel,
                    onAddClick = { navController.navigate(Routes.ADD_ITEM) }
                )
            }

            composable(Routes.ADD_ITEM) {
                AddItemScreen(
                    tradeViewModel = tradeViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.OFFERS) {
                OffersScreen(tradeViewModel = tradeViewModel)
            }
        }
    }
}
