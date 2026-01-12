package com.example.shoppingapp.Presentation.Screens.AuthScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun UserSIgnUpScreen(
    viewModel: MyViewModel = koinViewModel(),
    navController: NavController
) {

    var Email by remember { mutableStateOf("") }
    var Password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    // State listener
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.SignUpSuccess -> {
                navController.navigate(Routes.HomeScreenRoutes) {
                    popUpTo(Routes.SignUpScreenRoutes) { inclusive = true }
                }
                Toast.makeText(context, "Sign up successful!", Toast.LENGTH_LONG).show()
            }

            is AuthState.Error -> {
                Toast.makeText(
                    context,
                    (authState as AuthState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> Unit
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            // CARD CONTAINER
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // TITLE
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Fill the details below",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 20.dp, top = 4.dp)
                )

                // FIRST NAME
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // LAST NAME
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // EMAIL
                OutlinedTextField(
                    value = Email,
                    onValueChange = { Email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // PASSWORD
                OutlinedTextField(
                    value = Password,
                    onValueChange = { Password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // CONFIRM PASSWORD
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // PHONE NUMBER
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // ADDRESS
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))

                // BUTTON
                Button(
                    onClick = {
                        val data = UserDataModel(
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            email = Email.trim(),
                            password = Password.trim(),
                            phoneNumber = phoneNumber.trim(),
                            address = address.trim()
                        )

                        if (Password.trim() == confirmPassword.trim()) {
                            viewModel.createUser(data)
                        } else {
                            Toast.makeText(
                                context,
                                "Passwords do not match",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = firstName.isNotBlank() &&
                            lastName.isNotBlank() &&
                            Email.isNotBlank() &&
                            Password.isNotBlank() &&
                            confirmPassword.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sign Up")
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { navController.navigate(Routes.LoginScreenRoutes) }
                ) {
                    Text("Already have an account? Login")
                }
            }
        }
    }
}
