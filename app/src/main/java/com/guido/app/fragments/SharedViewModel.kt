package com.guido.app.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SharedViewModel @Inject constructor() :
    ViewModel() {



    private val _onLocationSelected: MutableLiveData<String> = MutableLiveData()
    val onLocationSelected: LiveData<String> = _onLocationSelected


    fun onLocationSelected(placeId : String){
        viewModelScope.launch {
            delay(1.seconds)
            _onLocationSelected.postValue(placeId)
        }
    }




}