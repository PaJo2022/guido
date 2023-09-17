package com.guido.app.data.tourData

import android.util.Log
import com.guido.app.api.ChatGptApi
import com.guido.app.api.VideoApi
import com.guido.app.log
import com.guido.app.model.chatGptModel.ChatGptRequest
import com.guido.app.model.chatGptModel.ChatGptResponse
import com.guido.app.model.videos.toUiModel
import com.guido.app.model.videosUiModel.VideoUiModel
import javax.inject.Inject

class TourDataRepositoryImpl @Inject constructor(
    private val api: ChatGptApi
) : TourDataRepository {
    override suspend fun getTourDataAboutTheLandMark(chatGptRequest: ChatGptRequest): ChatGptResponse? {
        return try {
            val response = api.fetchLocationDetailsFromChatGpt(chatGptRequest)
            response.body()
        }catch (e : Exception){
            null
        }
    }


}