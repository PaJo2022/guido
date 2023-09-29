package com.innoappsai.guido.api

import com.innoappsai.guido.model.chatGptModel.ChatGptRequest
import com.innoappsai.guido.model.chatGptModel.ChatGptResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatGptApi {

    @POST("completions")
    suspend fun fetchLocationDetailsFromChatGpt(
        @Body chatGptRequest: ChatGptRequest,
    ) : Response<ChatGptResponse>
}