package elnahas.com.nagwatask.data.api

import elnahas.com.nagwatask.data.model.FileResponseModel
import retrofit2.http.GET

interface ApiService {

    @GET("movies")
    suspend fun getFiles() : FileResponseModel
}