package com.kyf.knowyourfood.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : Screen(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Search : Screen(
        route = "search",
        title = "Search",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )

    data object Scanner : Screen(
        route = "scanner",
        title = "Scanner",
        selectedIcon = Icons.Filled.QrCodeScanner,
        unselectedIcon = Icons.Outlined.QrCodeScanner
    )

    data object Produce : Screen(
        route = "produce",
        title = "Produce & Plate",
        selectedIcon = Icons.Filled.Eco,
        unselectedIcon = Icons.Outlined.Eco
    )

    data object Profiles : Screen(
        route = "profiles",
        title = "Profiles",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    data object ProductDetail : Screen(
        route = "product_detail/{barcode}",
        title = "Product Details",
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info
    ) {
        fun createRoute(barcode: String) = "product_detail/$barcode"
    }
}
