package com.innoappsai.guido.data.tourData

import android.util.Log
import com.innoappsai.guido.api.ChatGptApi
import com.innoappsai.guido.model.chatGptModel.ChatGptRequest
import com.innoappsai.guido.model.chatGptModel.ChatGptResponse
import javax.inject.Inject

class TourDataRepositoryImpl @Inject constructor(
    private val api: ChatGptApi
) : ChatGptRepository {
    override suspend fun getTourDataAboutTheLandMark(chatGptRequest: ChatGptRequest): ChatGptResponse? {
        return try {
            val response = api.fetchLocationDetailsFromChatGpt(chatGptRequest)
            response.body()
        }catch (e : Exception){
            Log.i("JAPAN", "getTourDataAboutTheLandMark: ${e}")
            null
        }
    }


}