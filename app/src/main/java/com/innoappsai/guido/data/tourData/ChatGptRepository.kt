package com.innoappsai.guido.data.tourData

import com.google.gson.JsonObject
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel
import com.innoappsai.guido.model.chatGptModel.ChatGptRequest

interface ChatGptRepository {

    suspend fun getTourDataAboutTheLandMark(
        userDbId: String? = null, shouldSendEmail: Boolean = false, chatGptRequest: ChatGptRequest
    ): String?

    suspend fun getTravelItinerary(
        userDbId: String? = null, shouldSendEmail: Boolean = false, jsonObject: JsonObject
    ): ItineraryModel?
}