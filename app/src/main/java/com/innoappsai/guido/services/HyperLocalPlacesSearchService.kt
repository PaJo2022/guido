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
import com.innoappsai.guido.TextToSpeechHelper
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.data.tourData.ChatGptRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.db.MyAppDataBase
import com.innoappsai.guido.isDistanceOverNMeters
import com.innoappsai.guido.model.PlaceIdWithName.PlaceWithIdAndTime
import com.innoappsai.guido.model.chatGptModel.ChatGptRequest
import com.innoappsai.guido.model.chatGptModel.Message
import com.innoappsai.guido.model.places_backend_dto.PlaceDTO
import com.innoappsai.guido.model.places_backend_dto.toPlaceUiModel
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

    private lateinit var textToSpeechHelper: TextToSpeechHelper

    @Inject
    lateinit var placesRepository: PlacesRepository

    @Inject
    lateinit var chatGptRepository: ChatGptRepository

    @Inject
    lateinit var appPrefs: AppPrefs

    @Inject
    lateinit var locationClient: LocationClient

    @Inject
    lateinit var db: MyAppDataBase


    private val notificationChannelId = "Hyper Local Place Service"
    private val notificationChannelName = "Hyper Local Notifications"
    private val notificationId = 1
    private val PLACE_NOTIFICATION_ID = 2
    private var lastLocation: LatLng? = null



    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        isHyperLocalServiceIsRunning.value = true
        textToSpeechHelper = TextToSpeechHelper(this,object : TextToSpeechHelper.TextToSpeechCallback{
            override fun onError(errorMessage: String) {

            }

        })

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
                    delay(10.seconds)
                    continue
                }
                val interestList = placesRepository.getAllSavedPlaceTypePreferences()
                val response = placesRepository.fetchPlacesNearMeAndSaveInLocalDb(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    100,
                    interestList.map { it.id },
                    false
                )
                if (response.data.isNullOrEmpty()) {
                    delay(1.minutes)
                    continue
                }
                var place: PlaceDTO? = null
                val dao = db.placeIdWithTimeDao()
                val currentTimeMillis = System.currentTimeMillis()
                for (placeDTO in response.data) {
                    // Convert the time from double to milliseconds
                    val obj = db.placeIdWithTimeDao().getPlaceIdWithTime(placeDTO.placeId)
                    if (obj == null) {
                        place = placeDTO
                        dao.onPlacePushNotificationSend(
                            PlaceWithIdAndTime(
                                id = place.placeId,
                                lashPushNotificationShown = currentTimeMillis
                            )
                        )
                        break
                    }


                    // Calculate the time difference in milliseconds
                    val timeDifferenceMillis = currentTimeMillis - obj.lashPushNotificationShown

                    // Check if the time has passed 30 minutes (30 * 60 * 1000 milliseconds)
                    val thirtyMinutesInMillis = 15 * 60 * 1000
                    if (timeDifferenceMillis >= thirtyMinutesInMillis) {
                        place = placeDTO
                        dao.updateAllPlacesIsCheckedAndCheckBoxFor(
                                placeId = place.placeId,
                                lashPushNotificationShown = currentTimeMillis

                        )
                        break
                    }
                }
                if (place != null) {
                    val firstPlaceName = place.placeName
                    val placeUiModel = place.toPlaceUiModel()
                    val firstPlaceId = place.placeId
                    val message =
                        "I want you to act as a tour guide and generate a compelling guide details for the place also remember if you want have to introduce your self introduce as I am Guido AI, Your own personal travel assistance. Here are the details you can use:\n" +
                                "\n" +
                                "Place Name: ${placeUiModel.name}\n" +
                                "Street Address: ${placeUiModel.address}\n" +
                                "City: ${placeUiModel.city}\n" +
                                "State: ${placeUiModel.state}\n" +
                                "Country: ${placeUiModel.country}\n" +
                                "Contact Number: ${placeUiModel.callNumber}\n" +
                                "Website: ${placeUiModel.website}\n" +
                                "Instagram: ${placeUiModel.instagram}\n" +
                                "Facebook: ${placeUiModel.facebook}\n" +
                                "Business Email: ${placeUiModel.businessEmail}\n" +
                                "Business Owner: ${placeUiModel.name}\n" +
                                "Timings: ${placeUiModel.placeTimings}"
                    val chatGptResponse =
                        if (appPrefs.isTTSEnabled) chatGptRepository.getTourDataAboutTheLandMark(
                            chatGptRequest = ChatGptRequest(
                                listOf(Message(message, "user"))
                            )
                        ) else null
                    val notificationText =
                        "You are at ${firstPlaceName}, Want To Know More About It?"
                    // Display a local notification

                    withContext(Dispatchers.Main) {
                        updateNotification("Places Near You", notificationText, firstPlaceId)
                    }
                    chatGptResponse?.let { textToSpeechHelper.convertTextToSpeech(it) }
                }
                delay(appPrefs.hyperLocalSearchPoolingTime * 60 * 1000)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeechHelper.shutdown()
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
        placeId: String? = null
    ): NotificationCompat.Builder {
        Log.i("JAPAN", "createNotification: ${placeId}")

        val deepLinkIntent = Intent(this, MainActivity::class.java)
        deepLinkIntent.putExtra("PLACE_ID", placeId)
        deepLinkIntent.putExtra("DEEPLINK", "PLACE_DETAILS_SCREEN")
        val requestCode = placeId?.hashCode() ?: 0 // You can change this value if needed
        val flags =
            PendingIntent.FLAG_MUTABLE // Use FLAG_UPDATE_CURRENT to update the PendingIntent if it already exists
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            deepLinkIntent,
            flags
        )
        Log.i("JAPAN", "deepLinkIntent: ${deepLinkIntent.extras}")

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
        newContentText: String = "Checking Your Surrounding"
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
        newContentText: String = "Checking Your Surrounding",
        placeId: String? = null
    ) {
        val updatedNotification = createNotification(newContentTitle, newContentText, placeId)

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

