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
import androidx.compose.ui.graphics.Brush
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
import com.kyf.knowyourfood.ui.screens.analysis.PlateAnalysisScreen
import com.kyf.knowyourfood.ui.screens.food_detail.FoodDetailScreen
import com.kyf.knowyourfood.ui.screens.history.HistoryScreen
import com.kyf.knowyourfood.ui.screens.home.HomeScreen
import com.kyf.knowyourfood.ui.screens.home.HomeViewModel
import com.kyf.knowyourfood.ui.screens.plate.PlateScreen
import com.kyf.knowyourfood.ui.screens.plate.PlateViewModel
import com.kyf.knowyourfood.ui.screens.produce.ProduceScreen
import com.kyf.knowyourfood.ui.screens.produce.ProduceViewModel
import com.kyf.knowyourfood.ui.screens.product_detail.ProductDetailScreen
import com.kyf.knowyourfood.ui.screens.profiles.ProfilesScreen
import com.kyf.knowyourfood.ui.screens.profiles.ProfilesViewModel
import com.kyf.knowyourfood.ui.screens.recipes.RecipesScreen
import com.kyf.knowyourfood.ui.screens.scanner.BarcodeScannerScreen
import com.kyf.knowyourfood.ui.screens.scanner.ScannerViewModel
import com.kyf.knowyourfood.ui.screens.search.ProductSearchScreen
import com.kyf.knowyourfood.ui.screens.search.ProductSearchViewModel
import com.kyf.knowyourfood.ui.components.FloatingBottomBar
import com.kyf.knowyourfood.ui.screens.settings.SettingsScreen
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
        Screen.History,
        Screen.Scanner, // Central Floating FAB Action
        Screen.Plate,
        Screen.Profiles
    )

    val topLevelRoutes = listOf(
        Screen.Home.route,
        Screen.History.route,
        Screen.Scanner.route,
        Screen.Plate.route,
        Screen.Profiles.route
    )

    // Determine whether to show bottom navigation bar
    val showBottomBar = currentRoute in topLevelRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                FloatingBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(180)) + slideInHorizontally(
                    initialOffsetX = { it / 8 },
                    animationSpec = tween(180)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(140))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(180)) + slideInHorizontally(
                    initialOffsetX = { -it / 8 },
                    animationSpec = tween(180)
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(140))
            }
        ) {
            // 1. Home
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = viewModel {
                    HomeViewModel(app.profileRepository, app.productRepository, app.plateRepository)
                }
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToScanner = { navController.navigate(Screen.Scanner.route) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToProduce = { navController.navigate(Screen.Produce.route) },
                    onNavigateToPlate = { navController.navigate(Screen.Plate.route) },
                    onNavigateToProductDetail = { barcode ->
                        navController.navigate(Screen.ProductDetail.createRoute(barcode))
                    },
                    onNavigateToProfiles = { navController.navigate(Screen.Profiles.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) }
                )
            }

            // 2. History
            composable(Screen.History.route) {
                HistoryScreen(
                    productRepository = app.productRepository,
                    profileRepository = app.profileRepository,
                    onNavigateToProduct = { barcode ->
                        navController.navigate(Screen.ProductDetail.createRoute(barcode))
                    },
                    onNavigateToPlate = { navController.navigate(Screen.Plate.route) }
                )
            }

            // 3. Scanner
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

            // 4. Plate Builder
            composable(Screen.Plate.route) {
                val viewModel: PlateViewModel = viewModel {
                    PlateViewModel(app.plateRepository, app.profileRepository)
                }
                PlateScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduce = { navController.navigate(Screen.Produce.route) },
                    onNavigateToAnalysis = { navController.navigate(Screen.PlateAnalysis.route) },
                    onNavigateToRecipes = { navController.navigate(Screen.Recipes.route) }
                )
            }

            // 5. Profiles
            composable(Screen.Profiles.route) {
                val viewModel: ProfilesViewModel = viewModel {
                    ProfilesViewModel(app.profileRepository)
                }
                ProfilesScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // 6. Search
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

            // 7. Produce Explorer
            composable(Screen.Produce.route) {
                val viewModel: ProduceViewModel = viewModel {
                    ProduceViewModel(app.rawFoodRepository, app.plateRepository, app.profileRepository)
                }
                ProduceScreen(
                    viewModel = viewModel,
                    onNavigateToPlate = { navController.navigate(Screen.Plate.route) },
                    onNavigateToFoodDetail = { id -> navController.navigate(Screen.FoodDetail.createRoute(id)) }
                )
            }

            // 8. Food Detail
            composable(
                route = Screen.FoodDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                FoodDetailScreen(
                    foodId = id,
                    rawFoodRepository = app.rawFoodRepository,
                    plateRepository = app.plateRepository,
                    profileRepository = app.profileRepository,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPlate = { navController.navigate(Screen.Plate.route) }
                )
            }

            // 9. Plate Analysis
            composable(Screen.PlateAnalysis.route) {
                val viewModel: PlateViewModel = viewModel {
                    PlateViewModel(app.plateRepository, app.profileRepository)
                }
                PlateAnalysisScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToRecipes = { navController.navigate(Screen.Recipes.route) }
                )
            }

            // 10. Recipes
            composable(Screen.Recipes.route) {
                val viewModel: PlateViewModel = viewModel {
                    PlateViewModel(app.plateRepository, app.profileRepository)
                }
                RecipesScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 11. Settings
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProfiles = { navController.navigate(Screen.Profiles.route) }
                )
            }

            // 12. Product Detail
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
