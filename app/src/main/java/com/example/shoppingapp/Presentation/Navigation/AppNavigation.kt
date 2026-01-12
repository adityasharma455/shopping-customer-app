


package com.example.shoppingapp.Presentation.Navigation


import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.bottombar.AnimatedBottomBar
import com.example.bottombar.components.BottomBarItem
import com.example.bottombar.model.IndicatorDirection
import com.example.bottombar.model.IndicatorStyle
import com.example.shoppingapp.Presentation.Screens.AddEditAddressScreen.EditAddressScreen
import com.example.shoppingapp.Presentation.Screens.AuthScreens.UserLoginScreen
import com.example.shoppingapp.Presentation.Screens.AuthScreens.UserSIgnUpScreen
import com.example.shoppingapp.Presentation.Screens.BuyScreens.BuyCartProductScreen
import com.example.shoppingapp.Presentation.Screens.BuyScreens.BuySpecificProduct
import com.example.shoppingapp.Presentation.Screens.Screens.Category.AllCategoriesScreen
import com.example.shoppingapp.Presentation.Screens.Screens.Products.AllProductScreen
import com.example.shoppingapp.Presentation.Screens.Screens.Products.CartScreen
import com.example.shoppingapp.Presentation.Screens.Screens.HomeScreenSection.Home
import com.example.shoppingapp.Presentation.Screens.Screens.Products.WishListScreen
import com.example.shoppingapp.Presentation.Screens.ProfileScreens.UserProfile
import com.example.shoppingapp.Presentation.Screens.Screens.OrderScreens.OrdersScreen
import com.example.shoppingapp.Presentation.Screens.Screens.Products.AllProductsByCategory
import com.example.shoppingapp.Presentation.Screens.Screens.Products.AllSearchProducts
import com.example.shoppingapp.Presentation.Screens.Screens.Products.EachProductScreen
import com.google.firebase.auth.FirebaseAuth



@Composable
fun AppNavigation(firsebaseAuth: FirebaseAuth) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define all auth screen routes
    val authRoutes = setOf(
        Routes.LoginScreenRoutes,
        Routes.SignUpScreenRoutes
    )



    // Check if we should show bottom bar
    val showBottomBar = currentRoute != null &&
            !authRoutes.contains(currentRoute) &&
            firsebaseAuth.currentUser != null

    var startScreen = if (firsebaseAuth.currentUser == null) {
        SubNavigation.LogInSignUpScreenRoutes
    } else {
        SubNavigation.HomeProductScreenRoutes
    }

    val BottomNavItem = listOf(
        BottomItem(
            title = "Home",
            icon = Icons.Default.Home
        ),
        BottomItem(
            title = "Like",
            icon = Icons.Default.AddBox
        ),
        BottomItem(
            title = "cart",
            icon = Icons.Default.CardTravel
        ),
        BottomItem(
            title = "Profile",
            icon = Icons.Default.Person
        )
    )

    // SYNC BOTTOM NAV WITH CURRENT ROUTE
    var selectedItem by remember { mutableIntStateOf(0) }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                AnimatedBottomBar(
                    modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
                    selectedItem = selectedItem,
                    itemSize = BottomNavItem.size,
                    containerColor = Color.Transparent,
                    indicatorStyle = IndicatorStyle.FILLED,
                    indicatorColor = Color.Red,
                    indicatorDirection = IndicatorDirection.BOTTOM
                ) {
                    BottomNavItem.forEachIndexed { index, item ->
                        BottomBarItem(
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                                when (index) {
                                    0 -> {
                                        navController.navigate(Routes.HomeScreenRoutes){
                                            popUpTo(SubNavigation.HomeProductScreenRoutes){
                                                inclusive =true
                                            }
                                        }
                                    }
                                    1 -> {
                                        navController.navigate(Routes.WishListScreenRoutes)
                                    }
                                    2 -> {
                                        navController.navigate(Routes.CartScreenRoutes)
                                    }
                                    3 -> {
                                        navController.navigate(Routes.ProfileScreenRoutes)
                                    }
                                }
                            },
                            imageVector = item.icon,
                            label = item.title,
                            containerColor = Color.Transparent
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = startScreen
            ) {
                navigation<SubNavigation.LogInSignUpScreenRoutes>(
                    startDestination = Routes.LoginScreenRoutes
                ) {
                    composable<Routes.LoginScreenRoutes> {
                        UserLoginScreen(navController = navController)
                    }
                    composable<Routes.SignUpScreenRoutes> {
                        UserSIgnUpScreen(navController = navController)
                    }
                }

                navigation<SubNavigation.HomeProductScreenRoutes>(startDestination = Routes.HomeScreenRoutes) {
                    composable<Routes.HomeScreenRoutes> {
                        Home(navController = navController)
                    }
                    composable<Routes.AllProductsScreenRoutes> {
                        AllProductScreen(navController = navController)
                    }
                    composable<Routes.AllCategoriesScreenRoutes> {
                        AllCategoriesScreen(navController = navController)
                    }
                    composable<Routes.EditAdreessScreenRoutes> {
                        EditAddressScreen(navController)
                    }
                    composable<Routes.OrdersScreenRoutes> {
                        OrdersScreen(navController = navController)
                    }

                    composable<Routes.BuyCartProductScreenRoutes> {
                        Log.d("NAV_GRAPH", "BuyCartProductScreen composable is being called!")
                        BuyCartProductScreen(navController)
                    }
                    composable<Routes.WishListScreenRoutes> {
                        WishListScreen(navController = navController)
                    }
                    composable<Routes.AllProductByCategoryScreenRoutes> {
                        val data = it.toRoute<Routes.AllProductByCategoryScreenRoutes>()
                        AllProductsByCategory(
                            CategoryName = data.CategoryName,
                            navController = navController
                        )
                    }
                    composable<Routes.AllSearchProductsScreenRoutes> {
                        val data = it.toRoute<Routes.AllSearchProductsScreenRoutes>()
                        AllSearchProducts(
                            searchQuery = data.SearchQuery,
                            navController = navController
                        )
                    }
                    composable<Routes.BuyNowScreenRoutes> {
                        val data = it.toRoute<Routes.BuyNowScreenRoutes>()
                        BuySpecificProduct(
                            navController = navController,
                            productId = data.ProductId
                        )
                    }
                    composable<Routes.CartScreenRoutes> {
                        CartScreen(navController = navController)
                    }
                    composable<Routes.EachItemScreenRoutes> {
                        val data = it.toRoute<Routes.EachItemScreenRoutes>()
                        EachProductScreen(
                            ProductId = data.ProductId,
                            navController
                        )
                    }
                }

                navigation<SubNavigation.ProfileScreenRoutes>(startDestination = Routes.ProfileScreenRoutes) {
                    composable<Routes.ProfileScreenRoutes> {
                        UserProfile(navController = navController)
                    }
                }
            }
        }
    }
}



data class BottomItem(
    val title: String,
    val icon: ImageVector
)
