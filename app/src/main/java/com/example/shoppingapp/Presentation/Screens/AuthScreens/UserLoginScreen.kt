package com.example.shoppingapp.Presentation.Screens.AuthScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.shoppingapp.Domain.Models.UserDataModel
import com.example.shoppingapp.Presentation.Navigation.Routes
import com.example.shoppingapp.Presentation.ViewModel.AuthState
import com.example.shoppingapp.Presentation.ViewModel.MyViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserLoginScreen(
    viewModel: MyViewModel = koinViewModel(),
    navController: NavController
) {

    var Email by remember { mutableStateOf("") }
    var Password by remember { mutableStateOf("") }
    var context = LocalContext.current

    val authState by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.LoginSuccess -> {
                navController.navigate(Routes.HomeScreenRoutes) {
                    popUpTo(Routes.LoginScreenRoutes) { inclusive = true }
                }
            }

            is AuthState.Error -> {
                Toast.makeText(
                    context,
                    "Username or password is incorrect",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> Unit
        }
    }

    // Main Screen Layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        // Card-like box for better UI
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Title
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "Login to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp, top = 4.dp)
            )

            // Email Field
            OutlinedTextField(
                value = Email,
                onValueChange = { Email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Field
            OutlinedTextField(
                value = Password,
                onValueChange = { Password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Login Button
            Button(
                onClick = {
                    val data = UserDataModel(
                        email = Email.trim(),
                        password = Password.trim()
                    )

                    try {
                        viewModel.logInUser(data)
                    } catch (e: Exception) {
                        Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = Email.isNotBlank() && Password.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Signup Navigation
            TextButton(
                onClick = { navController.navigate(Routes.SignUpScreenRoutes) }
            ) {
                Text("Don't have an account? Sign Up")
            }
        }
    }
}
