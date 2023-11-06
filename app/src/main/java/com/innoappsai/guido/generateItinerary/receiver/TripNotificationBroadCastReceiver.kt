package com.innoappsai.guido.generateItinerary.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.transition.Transition
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
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
        val placeImage = intent.getStringExtra("TRIP_PLACE_IMAGE")

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_website)
            .setContentTitle("Your Next Trip To ${placeName} will start in 15 Minutes")
            .setContentText("Lets Go For Your Next Trip")


        // Build the notification
        if (isInternetAvailable(context)) {
            // Internet connection is available, load the Big Picture style notification.
            Glide.with(context)
                .asBitmap()
                .load(placeImage)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                        // Set the large image in the notification builder
                        builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(resource))
                            .setLargeIcon(resource)
                        notificationManager.notify(1, builder.build())
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Not used
                    }
                })
        } else {
            // No internet connection, create a simple notification.
            notificationManager.notify(1, builder.build())
        }

        // Show the notification

    }



    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }
}