package com.innoappsai.guido.model.review

import com.innoappsai.guido.model.User

data class Review(
    val title : String?,
    val description : String?,
    val rating : Double?,
    val user: User?
)
