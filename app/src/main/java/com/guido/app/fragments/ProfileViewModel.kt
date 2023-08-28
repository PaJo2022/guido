package com.guido.app.fragments

import androidx.lifecycle.ViewModel
import com.guido.app.data.places.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val placesRepository: PlacesRepository) :
    ViewModel() {



}