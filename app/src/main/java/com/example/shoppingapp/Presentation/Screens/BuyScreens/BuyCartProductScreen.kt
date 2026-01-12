package com.example.shoppingapp.Presentation.Screens.BuyScreens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.shoppingapp.Domain.Models.OrderDataModel
import com.example.shoppingapp.Presentation.Navigation.Routes
import com.example.shoppingapp.Presentation.ViewModel.CreateOrderScreen
import com.example.shoppingapp.Presentation.ViewModel.MyViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyCartProductScreen(
    navController: NavController,
    viewModel: MyViewModel = koinViewModel()
) {
    val cartState by viewModel.getAllCartProductsState.collectAsStateWithLifecycle()
    val userState by viewModel.getUserState.collectAsStateWithLifecycle()
    val createOrderState by viewModel.createOrderState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Get current user ID from Firebase Auth
    val currentUserId = Firebase.auth.currentUser?.uid ?: ""

    // Quantity state map
    val quantities: SnapshotStateMap<String, Int> = remember { mutableStateMapOf() }

    LaunchedEffect(Unit) {
        viewModel.getAllCartProducts()
        viewModel.getUser()
    }

    // Handle order creation state changes
    LaunchedEffect(createOrderState) {
        when (createOrderState) {
            is CreateOrderScreen.Success -> {
                // Show success toast and navigate back
                Toast.makeText(
                    context,
                    "Order placed successfully! 🎉",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetCreateOrderState()
                navController.popBackStack()
            }
            is CreateOrderScreen.Error -> {
                val errorMessage = (createOrderState as CreateOrderScreen.Error).error
                Toast.makeText(
                    context,
                    "Order failed: $errorMessage",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetCreateOrderState()
            }
            else -> {
                // Handle other states if needed
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Checkout Cart",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            if (cartState.isLoading == false && !cartState.isSuccess.isNullOrEmpty()) {
                val products = cartState.isSuccess

                // Calculate totals dynamically
                val subtotal = products?.sumOf { p ->
                    (p.finalprice?.toDoubleOrNull() ?: 0.0) * (quantities[p.productID] ?: 1)
                } ?: 0.0
                val discount = products?.sumOf { p ->
                    ((p.price?.toDoubleOrNull() ?: 0.0) - (p.finalprice?.toDoubleOrNull() ?: 0.0)) * (quantities[p.productID] ?: 1)
                } ?: 0.0
                val deliveryFee = 2.99
                val total = subtotal + deliveryFee

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Price Details",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal")
                                Text("${"%.2f".format(subtotal)} Rupees")
                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Discount")
                                Text("-${"%.2f".format(discount)} Rupees")
                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Delivery Fee")
                                Text("${"%.2f".format(deliveryFee)} Rupees")
                            }
                            Divider(Modifier.padding(vertical = 8.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Amount", fontWeight = FontWeight.Bold)
                                Text("${"%.2f".format(total)} Rupees", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val products = cartState.isSuccess
                            val user = userState.isSuccess

                            if (products != null && user != null) {
                                // Validate user is logged in
                                if (currentUserId.isBlank()) {
                                    Toast.makeText(context, "Please login to continue", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                // Create order object with all cart products
                                val order = OrderDataModel(
                                    userId = currentUserId,
                                    products = products,
                                    shippingAddress = user.address,
                                    customerName = "${user.firstName} ${user.lastName}",
                                    phoneNumber = user.phoneNumber,
                                    subtotal = subtotal,
                                    discount = discount,
                                    deliveryFee = deliveryFee,
                                    totalAmount = total,
                                    orderDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                                    paymentMethod = "Cash on Delivery",
                                    orderStatus = "Pending"
                                )

                                // Create the order
                                viewModel.createOrder(order)
                            } else {
                                Toast.makeText(context, "Please wait while we load your details", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = createOrderState !is CreateOrderScreen.Loading && // Disable when loading
                                !cartState.isSuccess.isNullOrEmpty() &&
                                userState.isSuccess != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        if (createOrderState is CreateOrderScreen.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Payment, contentDescription = "Buy Now")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buy Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        // Show loading state for order creation
        if (createOrderState is CreateOrderScreen.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Creating Order...", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        if (cartState.isLoading == true || userState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (cartState.Error != null || userState.Error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(cartState.Error ?: userState.Error ?: "Unknown Error")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        viewModel.getAllCartProducts()
                        viewModel.getUser()
                    }) {
                        Text("Retry")
                    }
                }
            }
        } else if (cartState.isSuccess.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Empty Cart",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Cart is Empty",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Add some products to checkout",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            val products = cartState.isSuccess ?: emptyList()
            val user = userState.isSuccess

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(products) { product ->
                    val quantity = quantities.getOrPut(product.productID) { 1 }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                product.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = product.image),
                                    contentDescription = product.name,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "${product.finalprice} Rupees",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                if (quantity > 1) quantities[product.productID] = quantity - 1
                                            },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.secondaryContainer,
                                                    CircleShape
                                                )
                                        ) {
                                            Text("-", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        }

                                        Text(" $quantity ", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                                        IconButton(
                                            onClick = { quantities[product.productID] = quantity + 1 },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.secondaryContainer,
                                                    CircleShape
                                                )
                                        ) {
                                            Text("+", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = { viewModel.removeProductFromCart(product) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }

                // Shipping info
                if (user != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Shipping Details",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = {
                                            navController.navigate(Routes.EditAdreessScreenRoutes)
                                        }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Address")
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Name: ${user.firstName} ${user.lastName}")
                                Text("Address: ${user.address}")
                                Text("Phone: ${user.phoneNumber}")
                            }
                        }
                    }
                }

                // Payment Method
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Payment Method",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Payment,
                                    contentDescription = "Payment",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Cash on Delivery")
                            }
                        }
                    }
                }
            }
        }
    }
}