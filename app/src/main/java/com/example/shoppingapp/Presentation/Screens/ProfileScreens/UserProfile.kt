package com.example.shoppingapp.Presentation.Screens.ProfileScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.shoppingapp.Presentation.Navigation.Routes
import com.example.shoppingapp.Presentation.ViewModel.AuthState
import com.example.shoppingapp.Presentation.ViewModel.MyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UserProfile(
    viewModel: MyViewModel = koinViewModel(),
    navController: NavController,
) {


        // State for editing mode
        var isEditing by remember { mutableStateOf(false) }
        // State for password visibility
        var isPasswordVisible by remember { mutableStateOf(false) }
        // Combined state for all user data

        // ViewModel states
        val getUserState by viewModel.getUserState.collectAsStateWithLifecycle()
        val updateUserDataState by viewModel.updateUserDataState.collectAsStateWithLifecycle()
    // Use derived state for userData to avoid unnecessary recompositions
    var userData by remember(getUserState) {
        mutableStateOf(getUserState.isSuccess)
    }
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    // Add state for showing confirmation dialog
    var showSignOutDialog by remember { mutableStateOf(false) }

    // ADD THIS: Handle auth state changes (sign out navigation)
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


    // Load user data when screen first appears - only once
    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            viewModel.getUser()
        }
    }

    // Handle update states

    LaunchedEffect(updateUserDataState) {
        when {
            updateUserDataState.Error != null -> {
                Toast.makeText(context, updateUserDataState.Error, Toast.LENGTH_SHORT).show()
            }
            updateUserDataState.isSuccess != null -> {
                Toast.makeText(context, updateUserDataState.isSuccess, Toast.LENGTH_SHORT).show()
                // Refresh data after successful update
                coroutineScope.launch(Dispatchers.IO) {
                    viewModel.getUser()
                }
                // Exit editing mode after successful update
                isEditing = false
            }
        }
    }



    // Loading/error states (unchanged)
    if (userData == null && getUserState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return // Exit early - don't show other content while loading
    }
    // Handle error state
    if (userData == null && getUserState.Error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: ${getUserState.Error}")
        }
        return // Exit early - don't show other content on error
    }




    // Main UI content (only shown when we have userData)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    // Sign out button with text
                    TextButton(
                        onClick = {showSignOutDialog = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            "Sign Out",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isEditing) {
                        // Save changes
                        userData?.let { data ->
                            coroutineScope.launch(Dispatchers.IO) {
                                viewModel.updateUserData(data)
                            }
                        }
                    }
                    isEditing = !isEditing
                },
                modifier = Modifier
                    .imePadding()
                    .navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        if (isEditing) Icons.Default.Lock else Icons.Default.Person,
                        contentDescription = if (isEditing) "Save" else "Edit",
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isEditing) "Save" else "Edit",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    ) { innerPadding ->

        // Add the confirmation dialog here at the end of your Column
        if (showSignOutDialog) {
            SignOutConfirmationDialog(
                onConfirm = {
                    viewModel.UserSignOut()
                    showSignOutDialog = false
                },
                onDismiss = { showSignOutDialog = false }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imeNestedScroll()
                .imePadding()
        ) {

            // Profile Picture Placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userData?.firstName?.firstOrNull()?.toString()?.uppercase() ?:"",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Personal Info Section
            Text(
                "Personal Information",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            // First Name and Last Name Row
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = userData?.firstName ?: "",
                    onValueChange = {newValues ->
                        if (isEditing){
                           userData = userData?.copy(firstName = newValues)
                        }
                                    },
                    label = { Text("First Name") },
                    readOnly = !isEditing,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                )

                OutlinedTextField(
                    value = userData?.lastName ?: "",
                    onValueChange = { newValues ->
                        if (isEditing) userData = userData?.copy(lastName = newValues) },
                    label = { Text("Last Name") },
                    readOnly = !isEditing,  modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                )
            }
            // Contact Info Section
            Text(
                "Contact Information",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Email Field
            OutlinedTextField(
                value = userData?.email ?: "",
                onValueChange = { newValues ->
                    if (isEditing) userData = userData?.copy(email = newValues) },
                label = { Text("Email") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                leadingIcon = { Icon(Icons.Default.Email, null) }
            )

            // Phone Field
            OutlinedTextField(
                value = userData?.phoneNumber ?: "",
                onValueChange = {newValues ->
                    if (isEditing) userData = userData?.copy(phoneNumber = newValues) },
                label = { Text("Phone Number") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                leadingIcon = {Icon(Icons.Default.Phone , contentDescription = null)}
            )

            // Address Field
            OutlinedTextField(
                value = userData?.address ?: "",
                onValueChange = { newValues ->
                    if (isEditing) userData = userData?.copy(address = newValues) },
                label = { Text("Address") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
               leadingIcon = {Icon(Icons.Default.Home, contentDescription = null)}
            )


            //Button To See all Orders
            //Button To See all Orders (Centered)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        navController.navigate(Routes.OrdersScreenRoutes)
                    }
                ) {
                    Text("View My Orders")
                }
            }
            // Only show password in edit mode
            if (isEditing) {
                OutlinedTextField(
                    value = userData?.password ?: "",
                    onValueChange = { if (isEditing) userData = userData?.copy(password = it) },
                    label = { Text("Password") },
                    visualTransformation = if (isPasswordVisible){
                        VisualTransformation.None
                    }else{
                        PasswordVisualTransformation()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = {isPasswordVisible = !isPasswordVisible}) {
                            Icon(
                                imageVector = if (isPasswordVisible){
                                    Icons.Default.Visibility
                                }else{
                                    Icons.Default.VisibilityOff
                                },
                                contentDescription = if (isPasswordVisible){
                                    "Hide Password"
                                }else{
                                    "show Password"
                                }
                            )
                        }
                    }

                )
            }


        }
    }
}
// Separate composable for the confirmation dialog
@Composable
fun SignOutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Confirm Sign Out",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "Are you sure you want to sign out?",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Sign Out")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

