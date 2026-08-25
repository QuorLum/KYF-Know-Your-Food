package com.kyf.knowyourfood.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.kyf.knowyourfood.KYFApplication
import com.kyf.knowyourfood.ui.screens.home.HomeScreen
import com.kyf.knowyourfood.ui.screens.home.HomeViewModel
import com.kyf.knowyourfood.ui.screens.plate.PlateScreen
import com.kyf.knowyourfood.ui.screens.plate.PlateViewModel
import com.kyf.knowyourfood.ui.screens.produce.ProduceScreen
import com.kyf.knowyourfood.ui.screens.produce.ProduceViewModel
import com.kyf.knowyourfood.ui.screens.product_detail.ProductDetailScreen
import com.kyf.knowyourfood.ui.screens.profiles.ProfilesScreen
import com.kyf.knowyourfood.ui.screens.profiles.ProfilesViewModel
import com.kyf.knowyourfood.ui.screens.scanner.BarcodeScannerScreen
import com.kyf.knowyourfood.ui.screens.scanner.ScannerViewModel
import com.kyf.knowyourfood.ui.screens.search.ProductSearchScreen
import com.kyf.knowyourfood.ui.screens.search.ProductSearchViewModel
import com.kyf.knowyourfood.ui.theme.*

@Composable
fun KYFNavHost(
    app: KYFApplication,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarScreens = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Scanner, // Central Action
        Screen.Produce,
        Screen.Profiles
    )

    // Determine whether to show bottom bar
    val showBottomBar = currentRoute != Screen.ProductDetail.route && currentRoute != "plate_view"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Slate900,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .border(1.dp, Slate800, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .navigationBarsPadding()
                ) {
                    bottomBarScreens.forEach { screen ->
                        val isSelected = currentRoute == screen.route

                        if (screen == Screen.Scanner) {
                            // Central Prominent Scanner Action Item
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(Emerald500),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.QrCodeScanner,
                                            contentDescription = "Scanner",
                                            tint = Slate950,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = "Scan",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Emerald400
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent
                                )
                            )
                        } else {
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                        contentDescription = screen.title,
                                        tint = if (isSelected) Emerald400 else Slate400
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 10.sp,
                                        color = if (isSelected) Emerald400 else Slate400
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Emerald500.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            }
        },
        containerColor = Slate950
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(200)) + slideInHorizontally(
                    initialOffsetX = { it / 6 },
                    animationSpec = tween(200)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(150))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(200)) + slideInHorizontally(
                    initialOffsetX = { -it / 6 },
                    animationSpec = tween(200)
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(150))
            }
        ) {
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = viewModel {
                    HomeViewModel(app.profileRepository, app.productRepository, app.plateRepository)
                }
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToScanner = { navController.navigate(Screen.Scanner.route) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToProduce = { navController.navigate(Screen.Produce.route) },
                    onNavigateToProductDetail = { barcode ->
                        navController.navigate(Screen.ProductDetail.createRoute(barcode))
                    },
                    onNavigateToProfiles = { navController.navigate(Screen.Profiles.route) }
                )
            }

            composable(Screen.Search.route) {
                val viewModel: ProductSearchViewModel = viewModel {
                    ProductSearchViewModel(app.productRepository)
                }
                ProductSearchScreen(
                    viewModel = viewModel,
                    onProductClick = { barcode ->
                        navController.navigate(Screen.ProductDetail.createRoute(barcode))
                    }
                )
            }

            composable(Screen.Scanner.route) {
                val viewModel: ScannerViewModel = viewModel {
                    ScannerViewModel(app.productRepository, app.profileRepository)
                }
                BarcodeScannerScreen(
                    viewModel = viewModel,
                    onNavigateToProductDetail = { barcode ->
                        navController.navigate(Screen.ProductDetail.createRoute(barcode))
                    }
                )
            }

            composable(Screen.Produce.route) {
                val viewModel: ProduceViewModel = viewModel {
                    ProduceViewModel(app.rawFoodRepository, app.plateRepository, app.profileRepository)
                }
                ProduceScreen(
                    viewModel = viewModel,
                    onNavigateToPlate = { navController.navigate("plate_view") }
                )
            }

            composable("plate_view") {
                val viewModel: PlateViewModel = viewModel {
                    PlateViewModel(app.plateRepository, app.profileRepository)
                }
                PlateScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduce = { navController.navigate(Screen.Produce.route) }
                )
            }

            composable(Screen.Profiles.route) {
                val viewModel: ProfilesViewModel = viewModel {
                    ProfilesViewModel(app.profileRepository)
                }
                ProfilesScreen(viewModel = viewModel)
            }

            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(navArgument("barcode") { type = NavType.StringType })
            ) { backStackEntry ->
                val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
                ProductDetailScreen(
                    barcode = barcode,
                    productRepository = app.productRepository,
                    profileRepository = app.profileRepository,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduct = { newBarcode ->
                        navController.navigate(Screen.ProductDetail.createRoute(newBarcode)) {
                            popUpTo(Screen.Search.route)
                        }
                    }
                )
            }
        }
    }
}
