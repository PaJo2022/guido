package com.guido.app.api

import com.guido.app.model.videos.YoutubeVideosDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoApi {

    @GET("/youtube/v3/search")
    suspend fun fetchVideosFromYoutube(
        @Query("part") part : String="snippet",
        @Query("q") q : String,
        @Query("maxResults") maxResults : Int = 5,
        @Query("key") key : String,
        @Query("order") order : String = "viewCount",
        @Query("type") type : String="video",
        @Query("videoDuration") videoDuration : String = "long",
    ) : Response<YoutubeVideosDTO>
}