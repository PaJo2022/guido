package com.innoappsai.guido.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.data.tourData.ChatGptRepository
import com.innoappsai.guido.model.chatGptModel.ChatGptRequest
import com.innoappsai.guido.model.chatGptModel.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentPlaceItinearyViewModel @Inject constructor(
    private val chatGptRepository: ChatGptRepository
) : ViewModel() {

    private val _isLoading: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isLoading: SharedFlow<Boolean> = _isLoading.asSharedFlow()

    private val _generatedItineary: MutableLiveData<String> = MutableLiveData()
    val generatedItineary: LiveData<String> = _generatedItineary

    fun generatePlaceItineary(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(true)
            val response = chatGptRepository.getTourDataAboutTheLandMark(
                ChatGptRequest(
                    listOf(Message(query, "user"))
                )
            )
            _isLoading.emit(false)
            _generatedItineary.postValue(response?.choices?.firstOrNull()?.message?.content.toString())
        }
    }
}