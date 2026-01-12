package com.example.shoppingapp.Presentation.Screens.AddEditAddressScreen

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.shoppingapp.Presentation.Screens.utlils.getAddressFromLatLng
import com.example.shoppingapp.Presentation.Screens.utlils.getCurrentLocation
import com.example.shoppingapp.Presentation.Screens.utlils.isLocationEnabled
import com.example.shoppingapp.Presentation.Screens.utlils.openLocationSettings
import com.example.shoppingapp.Presentation.ViewModel.MyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.accompanist.permissions.isGranted
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EditAddressScreen(
    navController: NavController,
    viewModel: MyViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val getUserState by viewModel.getUserState.collectAsStateWithLifecycle()
    val updateUserDataState by viewModel.updateUserDataState.collectAsStateWithLifecycle()

    var address by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var showLocationDialog by remember { mutableStateOf(false) }

    // Load saved user data into fields
    LaunchedEffect(getUserState.isSuccess) {
        getUserState.isSuccess?.let { user ->
            address = user.address ?: ""
            phoneNumber = user.phoneNumber ?: ""
        }
    }

    // Handle update feedback and navigate back after save
    LaunchedEffect(updateUserDataState) {
        when {
            updateUserDataState.isSuccess != null -> {
                Toast.makeText(context, updateUserDataState.isSuccess, Toast.LENGTH_SHORT).show()
                delay(500) // slight delay for state update
                navController.popBackStack()
            }
            updateUserDataState.Error != null -> {
                Toast.makeText(context, updateUserDataState.Error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Address & Phone") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                getUserState.isLoading -> CircularProgressIndicator()
                getUserState.Error != null -> Text("Error: ${getUserState.Error}")
                getUserState.isSuccess != null -> {
                    val user = getUserState.isSuccess
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = false
                        )

                        // Phone number field with +91 prefix and max 10 digits
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                if (it.length <= 10 && it.all { c -> c.isDigit() }) {
                                    phoneNumber = it
                                }
                            },
                            label = { Text("Phone Number (+91 )") },
                            placeholder = { Text("1234567890") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Button(
                            onClick = {
                                val isEnabled = isLocationEnabled(context)
                                if (!isEnabled) {
                                    showLocationDialog = true
                                    return@Button
                                }

                                if (locationPermissionState.status.isGranted) {
                                    getCurrentLocation(
                                        context = context,
                                        onSuccess = { lat, lon ->
                                            coroutineScope.launch {
                                                val addr = getAddressFromLatLng(context, lat, lon)
                                                if (addr != null) address = addr.getAddressLine(0) ?: ""
                                                else Toast.makeText(
                                                    context,
                                                    "Unable to fetch address",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        onError = { err -> Toast.makeText(context, err, Toast.LENGTH_SHORT).show() }
                                    )
                                } else {
                                    locationPermissionState.launchPermissionRequest()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Pick Current Location")
                        }

                        Button(
                            onClick = {
                                user?.let { nonNullUser ->
                                    val updatedUser = nonNullUser.copy(
                                        address = address,
                                        phoneNumber = "$phoneNumber"
                                    )
                                    coroutineScope.launch(Dispatchers.IO) {
                                        viewModel.updateUserData(updatedUser)
                                    }
                                }
                            }, enabled = phoneNumber.length==10,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (updateUserDataState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Save & Go Back")
                            }
                        }
                    }
                }
            }

            if (showLocationDialog) {
                AlertDialog(
                    onDismissRequest = { showLocationDialog = false },
                    title = { Text("Enable Location Services") },
                    text = { Text("Your location service is turned off. Please enable it to continue.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                openLocationSettings(context)
                                showLocationDialog = false
                            }
                        ) { Text("Open Settings") }
                    },
                    dismissButton = {
                        Button(onClick = { showLocationDialog = false }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}
