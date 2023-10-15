package com.innoappsai.guido.data.tourData

import android.util.Log
import com.innoappsai.guido.api.GuidoApi
import com.innoappsai.guido.model.chatGptModel.ChatGptRequest
import javax.inject.Inject

class TourDataRepositoryImpl @Inject constructor(
    private val api: GuidoApi
) : ChatGptRepository {
    override suspend fun getTourDataAboutTheLandMark(
        userDbId: String?,
        shouldSendEmail: Boolean,
        chatGptRequest: ChatGptRequest
    ): String? {
        return try {
            val response = api.askAI(userDbId,shouldSendEmail,chatGptRequest)
            response.body()
        } catch (e: Exception) {
            null
        }
    }


}