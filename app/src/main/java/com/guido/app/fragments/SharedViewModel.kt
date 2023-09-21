package com.guido.app.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() :
    ViewModel() {

    var shouldGoToSettings = false

    private val _onPreferencesSaved: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val onPreferencesSaved: MutableSharedFlow<Boolean> = _onPreferencesSaved

    private val _onLocationPermissionClicked: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val onLocationPermissionClicked: MutableSharedFlow<Boolean> = _onLocationPermissionClicked


    fun onPreferencesSaved() {
        viewModelScope.launch {
            delay(500)
            _onPreferencesSaved.emit(true)
        }
    }





    fun onLocationPermissionClicked() {
        viewModelScope.launch {
            _onLocationPermissionClicked.emit(true)
        }
    }





}