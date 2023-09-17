package com.guido.app.model.chatGptModel

data class ChatGptRequest(
    val messages: List<Message>,
    val model: String="gpt-3.5-turbo"
)