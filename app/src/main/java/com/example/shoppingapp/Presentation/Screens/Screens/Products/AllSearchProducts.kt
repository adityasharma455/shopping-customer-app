package com.example.shoppingapp.Presentation.Screens.Screens.Products

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.shoppingapp.Domain.Models.ProductDataModel
import com.example.shoppingapp.Presentation.Navigation.Routes
import com.example.shoppingapp.Presentation.Screens.Screens.Products.Utils.ProductCart
import com.example.shoppingapp.Presentation.ViewModel.MyViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllSearchProducts(
    viewModel: MyViewModel = koinViewModel<MyViewModel>(),
    navController: NavController,
    searchQuery: String
) {
    val searchState by viewModel.searchProductState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(searchQuery) {
        println("DEBUG: Searching for: $searchQuery")

        viewModel.onSearchQueryChanged(searchQuery)
        viewModel.SearchProduct(searchQuery)
    }
    LaunchedEffect(searchState.Error) {
        searchState.Error?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "All Search Products",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary // Title color
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary // Tint applied
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Background color
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = {viewModel.SearchProduct(searchQuery) }) { // <- call your search refresh method here
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onPrimary // Tint applied
                        )
                    }
                }
            )
        }



    ) { innerpadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerpadding),
        ) {
            when{
                searchState.isLoading ->{
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                searchState.Error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error loading products")
                        Toast.makeText(context, searchState.Error.toString(), Toast.LENGTH_SHORT)
                    }
                }
                searchState.isSuccess.isNullOrEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No products found for '$searchQuery'")
                    }
                }
                else ->{
                    val products = searchState.isSuccess ?: emptyList<ProductDataModel>()

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ){
                        items(products) {product ->
                            ProductCart(
                                product = product,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                onItemClick = {
                                    navController.navigate(Routes.EachItemScreenRoutes(product.productID))
                                }
                            )
                        }
                    }


                }
            }
        }
    }


            }


