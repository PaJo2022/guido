package com.innoappsai.guido.data.tourData

import com.innoappsai.guido.model.chatGptModel.ChatGptRequest

interface ChatGptRepository {

    suspend fun getTourDataAboutTheLandMark(
        userDbId: String? = null, shouldSendEmail: Boolean = false, chatGptRequest: ChatGptRequest
    ): String?
}