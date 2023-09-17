package com.guido.app.api

import com.guido.app.model.chatGptModel.ChatGptRequest
import com.guido.app.model.chatGptModel.ChatGptResponse
import com.guido.app.model.places.PlacesApiDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatGptApi {

    @POST("completions")
    suspend fun fetchLocationDetailsFromChatGpt(
        @Body chatGptRequest: ChatGptRequest,
    ) : Response<ChatGptResponse>
}