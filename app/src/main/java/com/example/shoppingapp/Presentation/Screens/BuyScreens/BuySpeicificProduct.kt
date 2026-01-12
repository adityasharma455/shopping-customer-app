package com.example.shoppingapp.Presentation.Screens.BuyScreens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
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
fun BuySpecificProduct(
    productId: String,
    navController: NavController,
    viewModel: MyViewModel = koinViewModel<MyViewModel>()
) {
    val productState by viewModel.getSpecificProductState.collectAsStateWithLifecycle()
    val userState by viewModel.getUserState.collectAsStateWithLifecycle()
    val createOrderState by viewModel.createOrderState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Get current user ID from Firebase Auth
    val currentUserId = Firebase.auth.currentUser?.uid ?: ""

    var quantity by remember { mutableStateOf(1) }

    // Load product and user data
    LaunchedEffect(productId) {
        viewModel.getSpecificProduct(productId)
    }

    LaunchedEffect(Unit) {
        viewModel.getUser()
    }

    // Handle order creation state changes - SIMPLIFIED
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
                val errorMessage = (createOrderState as CreateOrderScreen.Error)
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
                        "Check Out",
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
            if (productState.isSuccess != null && userState.isSuccess != null) {
                val product = productState.isSuccess
                val totalPrice = (product?.finalprice?.toDoubleOrNull() ?: 0.0) * quantity + 2.99

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${"%.2f".format(totalPrice)} Rupees",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = {
                            val product = productState.isSuccess
                            val user = userState.isSuccess

                            if (product != null && user != null) {
                                // Validate user is logged in
                                if (currentUserId.isBlank()) {
                                    Toast.makeText(context, "Please login to continue", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                // Create order object
                                val order = OrderDataModel(
                                    userId = currentUserId,
                                    products = listOf(product),
                                    shippingAddress = user.address,
                                    customerName = "${user.firstName} ${user.lastName}",
                                    phoneNumber = user.phoneNumber,
                                    subtotal = (product.finalprice.toDoubleOrNull() ?: 0.0) * quantity,
                                    discount = ((product.price.toDoubleOrNull() ?: 0.0) - (product.finalprice.toDoubleOrNull() ?: 0.0)) * quantity,
                                    deliveryFee = 2.99,
                                    totalAmount = ((product.finalprice.toDoubleOrNull() ?: 0.0) * quantity) + 2.99,
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
                                productState.isSuccess != null &&
                                userState.isSuccess != null,

                        modifier = Modifier
                            .height(48.dp)
                            .width(150.dp),
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
                            Icon(
                                Icons.Default.Payment,
                                contentDescription = "Buy Now",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buy Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->

        // Show full screen loading only for initial data load
        if (productState.isLoading && userState.isLoading && productState.isSuccess == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        // Handle product loading errors
        productState.Error?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error loading product details: $error")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        viewModel.getSpecificProduct(productId)
                    }) {
                        Text("Retry")
                    }
                }
            }
            return@Scaffold
        }

        // Handle user loading errors
        userState.Error?.let { error ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error loading user details: $error")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.getUser() }) {
                        Text("Retry")
                    }
                }
            }
            return@Scaffold
        }

        val product = productState.isSuccess
        val user = userState.isSuccess

        // Handle product not found
        if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Product not found")
            }
            return@Scaffold
        }

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Product Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Order Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Product details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Product image
                        Image(
                            painter = rememberAsyncImagePainter(model = product.image),
                            contentDescription = product.name,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Product info
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                product.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // Price display with discount if available
                            if (product.finalprice != product.price) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "${product.price} Rupees",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            textDecoration = TextDecoration.LineThrough
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "${product.finalprice} Rupees",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            } else {
                                Text(
                                    "${product.price} Rupees",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Quantity selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Quantity", style = MaterialTheme.typography.titleMedium)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        CircleShape
                                    )
                            ) {
                                Text("-", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }

                            Text(
                                " $quantity ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            IconButton(
                                onClick = {
                                    if (quantity < (product.availabelUnits.toIntOrNull() ?: 10)) quantity++
                                },
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
                }
            }

            // Shipping Information Card
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
                            style = MaterialTheme.typography.titleLarge,
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

                    Spacer(modifier = Modifier.height(16.dp))

                    if (user != null && user.address.isNotBlank()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Address",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "${user.firstName} ${user.lastName}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(user.address, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Phone,
                                        contentDescription = "Phone",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(user.phoneNumber, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(Routes.EditAdreessScreenRoutes)
                                }
                                .padding(16.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Add Address",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Add Shipping Address", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Method Card
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
                            "Payment Method",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                // Navigate to payment methods screen if available
                                Toast.makeText(context, "Change payment method", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Change Payment")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Default payment method
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = "Payment")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Cash on Delivery", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Price Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Price breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Price (${quantity} items)")
                        Text("${"%.2f".format((product.price.toDoubleOrNull() ?: 0.0) * quantity)} Rupees")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery Fee")
                        Text("2.99 Rupees")
                    }

                    if (product.finalprice != product.price) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Discount")
                            Text("-${"%.2f".format(
                                ((product.price.toDoubleOrNull() ?: 0.0) - (product.finalprice.toDoubleOrNull() ?: 0.0)) * quantity
                            )} Rupees")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Total Amount",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        val total = (product.finalprice.toDoubleOrNull() ?: 0.0) * quantity + 2.99
                        Text(
                            "${"%.2f".format(total)} Rupees",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}