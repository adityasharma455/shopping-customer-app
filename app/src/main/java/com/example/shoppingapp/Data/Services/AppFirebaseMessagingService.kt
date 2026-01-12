package com.example.shoppingapp.Services
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.shoppingapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import javax.inject.Inject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    companion object {
        private const val TAG = "FCM_SERVICE"
        private const val CHANNEL_ID = "new_product_channel"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            firebaseFirestore.collection("USER_FCM_TOKEN")
                .document(userId)
                .set(mapOf("token" to token))
                .addOnSuccessListener {
                    Log.d(TAG, "Token stored successfully for user: $userId")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to store token: ${e.message}")
                }
        } else {
            Log.w(TAG, "User not authenticated, cannot store token")
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a notification payload
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Notification Title: ${notification.title}")
            Log.d(TAG, "Notification Body: ${notification.body}")
            Log.d(TAG, "Notification Image: ${notification.imageUrl}")

            showNotification(
                notification.title ?: "New Product",
                notification.body ?: "Check out our latest addition!",
                notification.imageUrl?.toString() ?: ""
            )
        }

        // Check if message contains a data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            // You can handle data messages here
            val title = remoteMessage.data["title"] ?: "New Product"
            val body = remoteMessage.data["body"] ?: "Check out our new product!"
            val imageUrl = remoteMessage.data["image"] ?: ""

            showNotification(title, body, imageUrl)
        }
    }

    private fun showNotification(title: String, message: String, imageUrl: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel (required for Android 8.0+)
        createNotificationChannel(notificationManager)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
//            .setSmallIcon(R.drawable) // Make sure this exists
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        Log.d(TAG, "Notification displayed: $title")
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "New Product Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new products and offers"
                enableLights(true)
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }
}