package com.innoappsai.guido.data.videos

import android.util.Log
import com.innoappsai.guido.api.VideoApi
import com.innoappsai.guido.model.videos.toUiModel
import com.innoappsai.guido.model.videosUiModel.VideoUiModel
import javax.inject.Inject

class VideosRepositoryImpl @Inject constructor(
    private val api: VideoApi
) : VideoRepository {
    override suspend fun fetchPlacesVideos(query: String,apiKey : String): List<VideoUiModel> {
        return try {
            val response = api.fetchVideosFromYoutube(q = query, key = apiKey)
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