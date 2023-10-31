package com.innoappsai.guido.data.tourData

import android.util.Log
import com.google.gson.JsonObject
import com.innoappsai.guido.api.GuidoApi
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel
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

    override suspend fun getTravelItinerary(
        userDbId: String?,
        shouldSendEmail: Boolean,
        jsonObject : JsonObject
    ): ItineraryModel? {
        return try {
            val response = api.generateTravelItinerary(userDbId,shouldSendEmail,jsonObject)
            response.body()
        } catch (e: Exception) {
            null
        }
    }


}