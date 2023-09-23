package com.guido.app.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() :
    ViewModel() {

    var shouldGoToSettings = false

    private val _onPreferencesSaved: MutableLiveData<Boolean> = MutableLiveData()
    val onPreferencesSaved: MutableLiveData<Boolean> = _onPreferencesSaved

    private val _onLocationPermissionClicked: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val onLocationPermissionClicked: MutableSharedFlow<Boolean> = _onLocationPermissionClicked


    fun onPreferencesSaved() {
        viewModelScope.launch {
            _onPreferencesSaved.postValue(true)
        }
    }

    fun onPreferenceRead(){
        _onPreferencesSaved.value = false
    }


    fun onLocationPermissionClicked() {
        viewModelScope.launch {
            _onLocationPermissionClicked.emit(true)
        }
    }





}