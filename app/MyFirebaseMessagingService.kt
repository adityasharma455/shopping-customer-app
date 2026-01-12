

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.geniusapk.shopping.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {




    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // You can also store the token locally if needed
        val userId = firebaseAuth.currentUser?.uid ?: return
        firebaseFirestore.collection("user_tokens")
            .document(userId)
            .set(mapOf("token" to token))
            .addOnSuccessListener { Log.d("FCM", "Token stored successfully") }
            .addOnFailureListener { e -> Log.e("FCM", "Failed to store token: ${e.message}") }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Message received: ${remoteMessage.data}")

        // Handle notification
        remoteMessage.notification?.let {
            showNotification(it.title ?: "New Product", it.body ?: "Check out our new product!")
        }

        // Handle data payload
        if (remoteMessage.data.isNotEmpty()) {
            val productName = remoteMessage.data["product_name"]
            val imageUrl = remoteMessage.data["image_url"]
            // You can use this data to show a custom notification or update your UI
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "new_product_channel",
            "New Product Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, "new_product_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }
}