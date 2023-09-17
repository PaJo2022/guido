package com.guido.app.data.tourData

import com.guido.app.model.chatGptModel.ChatGptRequest
import com.guido.app.model.chatGptModel.ChatGptResponse
import com.guido.app.model.videosUiModel.VideoUiModel

interface TourDataRepository {

    suspend fun getTourDataAboutTheLandMark(
        chatGptRequest: ChatGptRequest
    ) : ChatGptResponse?
}