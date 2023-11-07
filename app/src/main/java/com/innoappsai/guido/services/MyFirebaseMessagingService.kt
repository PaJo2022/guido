package com.innoappsai.guido.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.innoappsai.guido.MainActivity
import com.innoappsai.guido.R
import com.innoappsai.guido.db.AppPrefs


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var appPrefs: AppPrefs


    companion object {
        private val notificationChannelId = "Travel Itinerary Service"
        private val notificationChannelName = "Travel Itinerary Service Notifications"
        private val notificationId = 1
        private val PLACE_NOTIFICATION_ID = 2
    }

    override fun onCreate() {
        super.onCreate()
        appPrefs = AppPrefs(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title.toString()
        val description = remoteMessage.notification?.body.toString()

        sendPushNotificationOnSuccessFullPlaceAdd(title, description, remoteMessage.data)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        appPrefs.fcmKey = token
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel =
                NotificationChannel(
                    notificationChannelId,
                    notificationChannelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendPushNotificationOnSuccessFullPlaceAdd(
        title: String,
        description: String,
        data: MutableMap<String, String>
    ) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val deepLinkIntent = Intent(this, MainActivity::class.java)
        val key = data["key"]
        val value = data["value"]
        deepLinkIntent.putExtra("DEEPLINK", key)
        deepLinkIntent.putExtra("VALUE", value)
        val requestCode = 0 // You can change this value if needed
        val flags =
            PendingIntent.FLAG_IMMUTABLE // Use FLAG_UPDATE_CURRENT to update the PendingIntent if it already exists
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            deepLinkIntent,
            flags
        )

        // Create a notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(
            this,
            notificationChannelId
        )
            .setSmallIcon(R.drawable.ic_website)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Show the notification
        notificationManager.notify(notificationId, notificationBuilder.build())
    }


}
