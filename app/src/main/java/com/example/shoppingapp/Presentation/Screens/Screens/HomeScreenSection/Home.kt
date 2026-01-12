package com.example.shoppingapp.Presentation.Screens.Screens.HomeScreenSection

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.shoppingapp.Presentation.Navigation.Routes
import com.example.shoppingapp.Presentation.Screens.Screens.HomeScreenSection.BannerUtilsSection.BannerCart
import com.example.shoppingapp.Presentation.Screens.Screens.Category.Utils.CategoryItem
import com.example.shoppingapp.Presentation.Screens.Screens.Products.Utils.ProductCart
import com.example.shoppingapp.Presentation.ViewModel.AuthState
import com.example.shoppingapp.Presentation.ViewModel.LoadHomeScreen
import com.example.shoppingapp.Presentation.ViewModel.MyViewModel
import com.example.shoppingapp.Presentation.ViewModel.SearchProductScreen
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(viewModel: MyViewModel = koinViewModel(), navController: NavController) {
    val homeDataState by viewModel.getHomedataState.collectAsStateWithLifecycle()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val searchState by viewModel.searchProductState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.clearSearchResults()
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.SignedOut -> {
                navController.navigate(Routes.LoginScreenRoutes) {
                    popUpTo(Routes.HomeScreenRoutes) { inclusive = true }
                }
            }
            is AuthState.Error -> {
                val errorMessage = (authState as AuthState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Shopping App",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (homeDataState.isLoading) {
                LoadingState()
            } else if (homeDataState.Error != null) {
                HomeErrorState(
                    error = homeDataState.Error!!,
                    onRetry = { /* Add retry logic */ }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Search Section
                    SearchSection(
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it; viewModel.onSearchQueryChanged(it) },
                        onClearSearch = { searchQuery = ""; viewModel.clearSearchResults() },
                        searchState = searchState,
                        onSearchNavigate = { query ->
                            if (searchState.isSuccess != null && query.isNotEmpty()) {
                                navController.navigate(Routes.AllSearchProductsScreenRoutes(query))
                            } else {
                                Toast.makeText(context, "Please enter a search term", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (searchQuery.isNotEmpty()) {
                        SearchResultsSection(
                            searchState = searchState,
                            searchQuery = searchQuery,
                            navController = navController,
                            context = context
                        )
                    } else {
                        // Main Content when not searching
                        HomeContent(
                            homeDataState = homeDataState,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Loading amazing products...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun HomeErrorState(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Oops! Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier
                .padding(horizontal = 32.dp)
        ) {
            Text("Try Again", fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun SearchSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    searchState: SearchProductScreen,
    onSearchNavigate: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = { Text("Discover products") },
                placeholder = { Text("Search anything...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.clickable { onSearchNavigate(searchQuery) }
                    )
                },
                trailingIcon = {
                    Row {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = onClearSearch) {
                                Icon(Icons.Default.Close, "Clear search")
                            }
                        }
                        if (searchState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
fun SearchResultsSection(
    searchState: SearchProductScreen,
    searchQuery: String,
    navController: NavController,
    context: Context
) {
    when {
        searchState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        searchState.Error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Search error: ${searchState.Error}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        searchState.isSuccess?.isNotEmpty() == true -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Search results header
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Search Results",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${searchState.isSuccess?.size} items found",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search results grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(searchState.isSuccess ?: emptyList()) { product ->
                        if (product?.productID?.isNotEmpty() == true) {
                            ProductCart(
                                product = product,
                                onItemClick = {
                                    navController.navigate(Routes.EachItemScreenRoutes(product.productID))
                                }
                            )
                        }
                    }
                }
            }
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No products found for '$searchQuery'")
            }
        }
    }
}

@Composable
fun HomeContent(
    homeDataState: LoadHomeScreen,
    navController: NavController, ) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Categories Section
        SectionHeader(
            title = "Categories",
            actionText = "See All",
            onActionClick = { navController.navigate(Routes.AllCategoriesScreenRoutes) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (homeDataState.isSuccessCategory != null) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(homeDataState.isSuccessCategory ?: emptyList()) { category ->
                    CategoryItem(
                        ImageUrl = category.categoryImage,
                        CategoryName = category.name,
                        onItemClick = {
                            navController.navigate(Routes.AllProductByCategoryScreenRoutes(category.name))
                        }
                    )
                }
            }
        } else {
            HomeEmptyStateSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                message = "No categories available"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Banner Section
        if (homeDataState.isSuccessBannerModel.isNullOrEmpty().not()) {
            BannerCart(
                BannerPhotos = homeDataState.isSuccessBannerModel ?: emptyList(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(180.dp),
                autoScrollInterval = 2000
            )
        } else if (!homeDataState.isLoading) {
            HomeEmptyStateSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 16.dp),
                message = "No banners available"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Featured Products Section
        SectionHeader(
            title = "Featured Products",
            actionText = "See All",
            onActionClick = { navController.navigate(Routes.AllProductsScreenRoutes) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (homeDataState.isSuccessProduct.isNullOrEmpty().not()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(homeDataState.isSuccessProduct ?: emptyList()) { product ->
                    ProductCart(
                        product = product,
                        onItemClick = {
                            navController.navigate(Routes.EachItemScreenRoutes(product.productID))
                        }
                    )
                }
            }
        } else {
            HomeEmptyStateSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                message = "No products available"
            )
        }

    }
}

@Composable
fun SectionHeader(title: String, actionText: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = actionText,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onActionClick),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun HomeEmptyStateSection(modifier: Modifier = Modifier, message: String) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

