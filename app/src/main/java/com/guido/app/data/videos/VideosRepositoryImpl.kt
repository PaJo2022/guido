package com.guido.app.data.videos

import android.util.Log
import com.guido.app.api.VideoApi
import com.guido.app.data.videos.VideoRepository
import com.guido.app.model.videos.toUiModel
import com.guido.app.model.videosUiModel.VideoUiModel
import javax.inject.Inject

class VideosRepositoryImpl @Inject constructor(
    private val api: VideoApi
) : VideoRepository {
    override suspend fun fetchPlacesVideos(query: String,apiKey : String): List<VideoUiModel> {
        return try {
            val response = api.fetchVideosFromYoutube(q = query, key = apiKey)
            Log.i("JAPAN", "DATA $response")
            if(response.isSuccessful){
                response.body()?.items?.toUiModel() ?: emptyList()
            }else{
                emptyList()
            }
        }catch (e : Exception){
            Log.i("JAPAN", "DATA $e")
            emptyList()
        }
    }


}