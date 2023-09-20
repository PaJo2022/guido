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


    private val _onPreferencesSaved: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val onPreferencesSaved: MutableSharedFlow<Boolean> = _onPreferencesSaved


    fun onPreferencesSaved() {
        viewModelScope.launch {
            delay(500)
            _onPreferencesSaved.emit(true)
        }
    }




}