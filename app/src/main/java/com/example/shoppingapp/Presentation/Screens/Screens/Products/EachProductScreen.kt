package com.example.shoppingapp.Presentation.Screens.Screens.Products


    import android.widget.Toast
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.ArrowBackIosNew
    import androidx.compose.material.icons.filled.Category
    import androidx.compose.material.icons.filled.CheckCircle
    import androidx.compose.material.icons.filled.Favorite
    import androidx.compose.material.icons.filled.FavoriteBorder
    import androidx.compose.material.icons.filled.Share
    import androidx.compose.material.icons.filled.Warning
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
    import androidx.compose.material3.TopAppBar
    import androidx.compose.material3.TopAppBarDefaults
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.getValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.style.TextDecoration
    import androidx.compose.ui.unit.dp
    import androidx.lifecycle.compose.collectAsStateWithLifecycle
    import androidx.navigation.NavController
    import coil3.compose.AsyncImage
    import com.example.shoppingapp.Presentation.Navigation.Routes
    import com.example.shoppingapp.Presentation.ViewModel.MyViewModel
    import com.example.shoppingapp.Presentation.ViewModel.WishListState
    import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EachProductScreen(
        ProductId: String,
        navController: NavController,
        viewModel: MyViewModel = koinViewModel()
    ) {

        val productState by viewModel.getSpecificProductState.collectAsStateWithLifecycle()
        val addToCartState by viewModel.addProductToCartState.collectAsStateWithLifecycle()
        val wishListState by viewModel.wishListState.collectAsStateWithLifecycle()


        val currentWishListState = wishListState[ProductId] ?: WishListState()
        val context = LocalContext.current
        val ProductId = ProductId

        LaunchedEffect(ProductId) {
            viewModel.getSpecificProduct(ProductId)
        }
        LaunchedEffect(ProductId) {
            viewModel.checkIfItemInWishList(ProductId)

        }


        // Handle wishlist operation results
        LaunchedEffect(currentWishListState.lastAction, currentWishListState.Error) {
            currentWishListState.lastAction?.let { action ->
                val message =
                    if (action == "added") "Added to Wishlist" else "Removed from Wishlist"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                // Reset lastAction to prevent duplicate toasts
                viewModel.clearWishListActionState(ProductId)
            }

            currentWishListState.Error?.let { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                viewModel.clearWishListErrorState(ProductId)
            }

        }

        // Add this LaunchedEffect for handling results
        LaunchedEffect(addToCartState.isSuccess, addToCartState.Error) {
            addToCartState.isSuccess?.let { success ->
                if (success) {
                    Toast.makeText(context, "Added to cart successfully!", Toast.LENGTH_SHORT).show()
                    viewModel.resetAddToCartState()
                }
            }

            addToCartState.Error?.let { error ->
                Toast.makeText(context, "Failed to add to cart: $error", Toast.LENGTH_SHORT).show()
                viewModel.resetAddToCartState()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Product Details",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary  // Added color consistency
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary  // Added tint
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,  // Added colors
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }

        ) { innerPadding ->

            when {
                productState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                productState.Error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = " Error Loadding the Product",
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { viewModel.getSpecificProduct(ProductId) }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                productState.isSuccess != null -> {
                    val product = productState.isSuccess
                    Column(
                        modifier = Modifier.fillMaxSize().padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                    ) {
                        //Product Image gallery
                        Box(
                            modifier = Modifier.fillMaxWidth().height(300.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = product?.image ?: "default_image_url",
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }

                        //Product Details
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Name and WishList
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = product?.name ?: "Shopping App Product ",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                // Wishlist Icon Button - USING toggleWishList with FavDataModel
                                IconButton(
                                    onClick = {
                                        productState.isSuccess?.let { product ->
                                            if (product != null) {
                                                viewModel.toggleWishList(product)
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Product is Missing",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } ?: run {
                                            Toast.makeText(
                                                context,
                                                "Product not loaded",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    enabled = !currentWishListState.isLoading && (product?.productID?.isNotBlank() == true)
                                ) {
                                    if (currentWishListState.isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    } else {
                                        Icon(
                                            imageVector = if (currentWishListState.isInWishList == true) {
                                                Icons.Default.Favorite
                                            } else {
                                                Icons.Default.FavoriteBorder
                                            },
                                            contentDescription = "Toggle wishlist",
                                            tint = if (currentWishListState.isInWishList == true) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Price Section
                            if (product!!.finalprice != product.price) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "₹${product.price}",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            textDecoration = TextDecoration.LineThrough
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "₹${product.finalprice}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            } else {
                                Text(
                                    text = "₹${product.price}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))


                            // Category and Availability
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Category Card
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 8.dp
                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Category,
                                            contentDescription = "Category",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = product.category,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Availability Card
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 8.dp
                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (product.availabelUnits.toIntOrNull() ?: 0 > 0) {
                                                Icons.Default.CheckCircle
                                            } else {
                                                Icons.Default.Warning
                                            },
                                            contentDescription = "Availability",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${product.availabelUnits} available",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Description
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product.description,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.height(32.dp))




                            Button(
                                onClick = {
                                    navController.navigate(Routes.BuyNowScreenRoutes(ProductId))
                                },                                    modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Buy Now"
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            // Your button section:
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (addToCartState.isLoading != true) {
                                            viewModel.addProductToCart(product)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = addToCartState.isLoading != true &&
                                            (product.availabelUnits.toIntOrNull() ?: 0 > 0)
                                ) {
                                    if (addToCartState.isLoading == true) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Adding to Cart...")
                                    } else {
                                        Text("Add to Cart")
                                    }
                                }

                                // Show error message if any
                                addToCartState.Error?.let { error ->
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(top = 4.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                        }
                    }
                }


            }
        }
    }