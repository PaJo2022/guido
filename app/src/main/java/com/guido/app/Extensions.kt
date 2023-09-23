package com.guido.app

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


fun <T> Flow<T>.collectIn(
    lifecycleOwner: LifecycleOwner,
    onEachValue: (T) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        collect { value ->
            onEachValue(value)
        }
    }
}

fun Activity.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Any?.log(){
    if(BuildConfig.DEBUG){
        Log.i("JAPAN", "log: ${this}")
    }
}

fun View.isVisibleAndEnable(value : Boolean){
    this.visibility =  if (value) View.VISIBLE else View.INVISIBLE
    this.isEnabled = value
}


fun Context.getScreenWidth(): Int {
    return resources.displayMetrics.widthPixels
}

fun Context.getScreenHeight(): Int {
    return resources.displayMetrics.heightPixels
}


fun Any.safeDouble(): Double {
    val anyToString = this.toString()
    return try {
        anyToString.toDouble()
    }catch (e : Exception){
        0.0
    }
}


fun EditText.setNullText(text: String?) {
    text?.let {
        this.setText(it)
    } ?: this.text.clear()
}

fun View.toggleEnableAndAlpha(isEnable: Boolean) {
    this.isEnabled = isEnable
    if (isEnable) {
        this.alpha = 1f
    } else {
        this.alpha = 0.3f
    }
}

fun View.toggleEnableAndVisibility(isEnable: Boolean) {
    this.isEnabled = isEnable
    if (isEnable) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.INVISIBLE
    }
}

fun Fragment.addOnBackPressedCallback(
    isEnabled: Boolean = true,
    onBackPressedAction: () -> Unit
) {
    val callback = object : OnBackPressedCallback(isEnabled) {
        override fun handleOnBackPressed() {
            onBackPressedAction.invoke()
        }
    }
    requireActivity().onBackPressedDispatcher.addCallback(this, callback)
}