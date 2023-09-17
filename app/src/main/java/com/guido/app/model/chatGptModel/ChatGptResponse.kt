package com.guido.app.model.chatGptModel

data class ChatGptResponse(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val usage: Usage
)