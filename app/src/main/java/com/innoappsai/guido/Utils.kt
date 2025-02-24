package com.innoappsai.guido

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val radius = 6371 // Earth's radius in kilometers

    // Convert latitude and longitude from degrees to radians
    val lat1Rad = Math.toRadians(lat1)
    val lon1Rad = Math.toRadians(lon1)
    val lat2Rad = Math.toRadians(lat2)
    val lon2Rad = Math.toRadians(lon2)

    // Calculate the differences
    val dLat = lat2Rad - lat1Rad
    val dLon = lon2Rad - lon1Rad

    // Calculate the Haversine distance
    val a = sin(dLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    // Calculate the distance in meters
    val distance = radius * c * 1000 // Convert to meters

    return distance
}

fun formatDouble(doubleValue: Double): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    numberFormat.maximumFractionDigits = 2
    return numberFormat.format(doubleValue)
}

fun formatMillisToDateString(millis: Long, dateFormat: String): String {
    val date = Date(millis)
    val sdf = SimpleDateFormat(dateFormat, Locale.US)
    return sdf.format(date)
}

fun isEmailValid(email: String): Boolean {
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    return email.matches(emailPattern.toRegex())
}

fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}


fun getImageBytes(imageFile: File): ByteArray {
    val stream = ByteArrayOutputStream()
    val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream)
    return stream.toByteArray()
}

fun sendEmail(context: Context, emailAddress: String) {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:$emailAddress")
//        intent.putExtra(Intent.EXTRA_EMAIL, emailAddress)
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry about Your Business Found Online")
//        intent.putExtra(Intent.EXTRA_TEXT, emailBody)
    context.startActivity(intent)
}

fun callToNumber(context: Context, number: String) {
    val intent = Intent(Intent.ACTION_CALL);
    intent.data = Uri.parse("tel:${number}")
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Can Not Call On The Number", Toast.LENGTH_SHORT).show()
    }
}

fun TextView.makeTextViewClickableLink(webUrlOrMobileNumber: String?,errorMessage : String, onClicked: () -> Unit) {
    if (webUrlOrMobileNumber.isNullOrEmpty()) {
        // If websiteUrl is null or empty, display the TextView as normal text
        text = errorMessage
        setOnClickListener(null) // Remove any click listener
    } else {
        // Create a ClickableSpan to handle website clicks
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle website click here
                onClicked()
            }
        }

        // Create a SpannableString with the ClickableSpan
        val spannableString = SpannableString(webUrlOrMobileNumber)
        spannableString.setSpan(
            clickableSpan,
            0,
            webUrlOrMobileNumber.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the SpannableString to the TextView
        text = spannableString

        // Enable the TextView to handle link clicks
        movementMethod = android.text.method.LinkMovementMethod.getInstance()
    }

}

fun openWebsite(context: Context, url: String) {
    var webUrl = url
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        webUrl = "http://$url"
    }

    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
    try {
        context.startActivity(browserIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "Can Not Open The Website", Toast.LENGTH_SHORT).show()
    }

}

fun openDirection(context: Context, placeName: String?, latLng: LatLng?) {
    val geoUri =
        "http://maps.google.com/maps?q=loc:${latLng?.latitude},${latLng?.longitude}($placeName)"

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Can Not Open The Maps", Toast.LENGTH_SHORT).show()
    }
}
fun calculateDistanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371.0 // Earth's radius in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}


enum class TraveingType{
    WALKING,DRIVING
}
fun calculateTravelTimeAndMode(
    originLat: Double,
    originLon: Double,
    destinationLat: Double,
    destinationLon: Double
): Pair<TraveingType, String> {
    val distance = calculateDistanceInKm(originLat, originLon, destinationLat, destinationLon)
    val walkingSpeedKmph = 5.0 // Average walking speed in km/h
    val drivingSpeedKmph = 50.0 // Average driving speed in km/h

    val walkingTimeHours = distance / walkingSpeedKmph
    val drivingTimeHours = distance / drivingSpeedKmph

    val walkingTimeMinutes = (walkingTimeHours * 60).toInt()
    val drivingTimeMinutes = (drivingTimeHours * 60).toInt()

    val travelMode: TraveingType
    val travelTime: String

    if (distance < 5.0) {
        travelMode = TraveingType.WALKING
        if (walkingTimeMinutes >= 60) {
            val walkingTimeHoursInt = walkingTimeMinutes / 60
            val walkingTimeMinutesInt = walkingTimeMinutes % 60
            travelTime = "$walkingTimeHoursInt hours ${walkingTimeMinutesInt} minutes"
        } else {
            travelTime = "$walkingTimeMinutes minutes"
        }
    } else {
        travelMode = TraveingType.DRIVING
        if (drivingTimeMinutes >= 60) {
            val drivingTimeHoursInt = drivingTimeMinutes / 60
            val drivingTimeMinutesInt = drivingTimeMinutes % 60
            travelTime = "$drivingTimeHoursInt hours ${drivingTimeMinutesInt} minutes"
        } else {
            travelTime = "$drivingTimeMinutes minutes"
        }
    }

    return Pair(travelMode, travelTime)
}



fun showDirectionsOnGoogleMaps(
    context: Context,
    sourceLat: Double,
    sourceLon: Double,
    sourceName: String,
    destinationLat: Double,
    destinationLon: Double,
    destinationName: String
) {
    val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=$sourceLat,$sourceLon&origin_place=$sourceName&destination=$destinationLat,$destinationLon&destination_place=$destinationName")
    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
    mapIntent.setPackage("com.google.android.apps.maps") // Ensure Google Maps is used if available

    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    } else {
        // Handle the case where Google Maps is not installed
        // You can open a web browser with the same URL as a fallback option
        val webIntent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(webIntent)
    }
}


fun updateApiKey(originalString: String?): String {
    // Check if the originalString contains "GCP_KEY"
    if (originalString?.contains("GCP_API_KEY") == true) {
        // Replace "GCP_KEY" with the actual API key
        val replacedString = originalString.replace("GCP_API_KEY", "_AIzaSyBLXHjQ9_gyeSoRfndyiAz0lfvm-3fgpxY")
        return replacedString
    } else {
        // If "GCP_KEY" is not found, return the original string as is
        return originalString.toString()
    }
}

fun generateStaticMapUrl(latitude: Double, longitude: Double): String {
    val apiKey =
        "AIzaSyBLXHjQ9_gyeSoRfndyiAz0lfvm-3fgpxY" // Replace with your Google Maps API key
    val marker = "icon:http://www.google.com/mapfiles/arrow.png|$latitude,$longitude"
    val size = "200x200" // Adjust the size as needed

    return "https://maps.googleapis.com/maps/api/staticmap?center=${latitude},${longitude}&zoom=13&size=600x300&maptype=roadmap&markers=${latitude},${longitude}&key=${apiKey}"
}

fun isURLValid(urlString: String): Boolean {
    try {
        // Attempt to create a URL object from the given string
        val url = URL(urlString)

        // Check if the URL is well-formed
        url.toURI()

        // Additional checks for specific URL patterns if needed
        // For example, you can check if it uses a valid protocol (e.g., http, https)
        // if (url.protocol != "http" && url.protocol != "https") {
        //     return false
        // }

        // If no exceptions are thrown, the URL is valid
        return true
    } catch (e: Exception) {
        // Malformed URL or other exceptions indicate it's not valid
        return false
    }
}

fun isDistanceOverNMeters(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double,
    meters : Double
): Boolean {
    val distance = calculateDistance(lat1, lon1, lat2, lon2)
    Log.i("JAPAN", "distance: ${distance}")
    return distance > meters
}

fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val activityManager = context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager

    // Get a list of the currently running services
    val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)

    // Loop through the running services and check if the specified service is running
    for (service in runningServices) {
        if (service.service.className == serviceClass.name) {
            // The service is running
            return true
        }
    }

    // The service is not running
    return false
}

fun convertToAMPM(hour: Int): String {
    return if (hour in 0..23) {
        if (hour < 12) {
            if (hour == 0) "12 AM" else "$hour AM"
        } else {
            if (hour == 12) "12 PM" else "${hour - 12} PM"
        }
    } else {
        "6 AM"
    }
}