package com.guido.app.model.chatGptModel

data class Choice(
    val finish_reason: String,
    val index: Int,
    val message: Message
)