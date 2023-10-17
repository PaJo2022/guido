package com.innoappsai.guido.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.maps.model.LatLng
import com.innoappsai.guido.LocationClient
import com.innoappsai.guido.MainActivity
import com.innoappsai.guido.MyApp.Companion.isHyperLocalServiceIsRunning
import com.innoappsai.guido.R
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.isDistanceOverNMeters
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class HyperLocalPlacesSearchService : Service() {

    @Inject
    lateinit var placesRepository: PlacesRepository

    @Inject
    lateinit var appPrefs: AppPrefs

    @Inject
    lateinit var locationClient: LocationClient

    private val notificationChannelId = "Hyper Local Place Service"
    private val notificationChannelName = "Hyper Local Notifications"
    private val notificationId = 1
    private val PLACE_NOTIFICATION_ID = 2
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var lastLocation: LatLng? = null

    private val notificationIntent by lazy {
        val intent = Intent(this, MainActivity::class.java)
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private var calls = 0

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        isHyperLocalServiceIsRunning.value = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        // Start the foreground service

        startForeground(notificationId, createNotification().build())
        createPlaceNotification().build()
        // Start the periodic task using coroutines
        CoroutineScope(Dispatchers.IO).launch {
            isHyperLocalServiceIsRunning.postValue(true)
            while (true) {
                // Perform API request (replace with your own logic)
                // val response = performApiRequest(YOUR_API_URL)

                // For demonstration purposes, let's assume we got some data
                val currentLocation = locationClient.getCurrentLocation() ?: continue
                val shouldFetchApi = if (lastLocation == null) true else isDistanceOverNMeters(
                    lastLocation!!.latitude,
                    lastLocation!!.longitude,
                    currentLocation.latitude,
                    currentLocation.longitude,
                    200.0
                )
                lastLocation = currentLocation
                if (!shouldFetchApi) {
                    continue
                }
                val interestList = placesRepository.getAllSavedPlaceTypePreferences()
                val response = placesRepository.fetchPlacesNearMeAndSaveInLocalDb(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    appPrefs.prefDistance,
                    interestList.map { it.id }
                )

                val notificationText =
                    "There are ${response.data?.size} Places near you"
                // Display a local notification
                withContext(Dispatchers.Main) {
                    updateNotification("Places Near You", notificationText)
                }
                delay(5.minutes)

            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isHyperLocalServiceIsRunning.value = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
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

    private fun createNotification(
        newContentTitle: String = "",
        newContentText: String = "",
        imageUrl: String? = null
    ): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle(newContentTitle)
            .setContentText(newContentText)
            .setSmallIcon(R.drawable.ic_website)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
    }

    private fun createPlaceNotification(
        newContentTitle: String = "Searching Places Near You",
        newContentText: String = "Checking Your Surrounding",
        imageUrl: String? = null
    ): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle(newContentTitle)
            .setContentText(newContentText)
            .setSmallIcon(R.drawable.ic_website)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
    }

    private fun updateNotification(
        newContentTitle: String = "Searching Places Near You",
        newContentText: String = "Checking Your Surrounding"
    ) {
        val updatedNotification = createNotification(newContentTitle, newContentText)

        // Use NotificationManagerCompat to update the notification
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@HyperLocalPlacesSearchService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(PLACE_NOTIFICATION_ID, updatedNotification.build())
        }
    }


}

