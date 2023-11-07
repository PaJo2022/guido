package com.innoappsai.guido.data.tourData

import com.innoappsai.guido.api.GuidoApi
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel
import com.innoappsai.guido.model.chatGptModel.ChatGptRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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

    override suspend fun getTravelItinerary(userId: String, query: String): ItineraryModel? {
        return try {
            val body: RequestBody = query.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = api.generateTravelItinerary(userId, body)
            response.body()
        } catch (e: Exception) {
            null
        }
    }


}