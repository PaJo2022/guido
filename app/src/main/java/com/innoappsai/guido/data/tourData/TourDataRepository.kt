package com.innoappsai.guido.data.tourData

import com.innoappsai.guido.model.chatGptModel.ChatGptRequest
import com.innoappsai.guido.model.chatGptModel.ChatGptResponse

interface TourDataRepository {

    suspend fun getTourDataAboutTheLandMark(
        chatGptRequest: ChatGptRequest
    ) : ChatGptResponse?
}