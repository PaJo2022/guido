package com.guido.app

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayOutputStream
import java.io.File
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
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
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

fun updateApiKey(originalString: String?): String {
    // Check if the originalString contains "GCP_KEY"
    if (originalString?.contains("GCP_API_KEY") == true) {
        // Replace "GCP_KEY" with the actual API key
        val replacedString = originalString.replace("GCP_API_KEY", "AIzaSyBLXHjQ9_gyeSoRfndyiAz0lfvm-3fgpxY")
        return replacedString
    } else {
        // If "GCP_KEY" is not found, return the original string as is
        return originalString.toString()
    }
}
