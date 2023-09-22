package com.guido.app

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat.startActivity
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

fun sendEmail(context : Context, emailAddress : String){
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:$emailAddress")
//        intent.putExtra(Intent.EXTRA_EMAIL, emailAddress)
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry about Your Business Found Online")
//        intent.putExtra(Intent.EXTRA_TEXT, emailBody)
    context.startActivity(intent)
}

fun callToNumber(context: Context,number : String){
    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
    context.startActivity(intent)
}