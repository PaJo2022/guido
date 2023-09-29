package com.innoappsai.guido.model.chatGptModel

data class Choice(
    val finish_reason: String,
    val index: Int,
    val message: Message
)