    package com.example.shoppingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.shoppingapp.Presentation.Navigation.AppNavigation
import com.example.shoppingapp.ui.theme.ShoppingAppTheme
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject
import javax.inject.Inject
import kotlin.getValue

    class MainActivity : ComponentActivity() {

    private val firebaseAuth: FirebaseAuth by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingAppTheme {
                AppNavigation(firebaseAuth)
            }
        }
    }
}

