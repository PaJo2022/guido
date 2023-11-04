package com.innoappsai.guido.generateItinerary.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.innoappsai.guido.R

class TripNotificationBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Code to execute when the alarm triggers
        // Show a notification here
        val NOTIFICATION_CHANNEL_ID = "TRIP_NOTIFICATION"
        val NOTIFICATION_CHANNEL_NAME = "Trip Notification Channel"
        val PUSH_NOTIFICATION_ID = 1
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = NOTIFICATION_CHANNEL_ID
            val channelName = NOTIFICATION_CHANNEL_NAME
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }
        Log.i("JAPAN", "onReceive: ${intent}")
        val placeName = intent.getStringExtra("TRIP_PLACE_NAME")

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_website)
            .setContentTitle("15 Minutes Left For Your Next Trip")
            .setContentText("Lets Start Your Next Trip At ${placeName}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)

        // Show the notification
        notificationManager.notify(PUSH_NOTIFICATION_ID, notificationBuilder.build())
    }
}