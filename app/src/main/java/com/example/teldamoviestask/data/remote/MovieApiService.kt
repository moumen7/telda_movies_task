package com.example.teldamoviestask.data.remote

import com.example.teldamoviestask.model.MovieResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MovieApiService {
    @GET("discover/movie?include_adult=false&include_video=false&language=en-US&sort_by=popularity.desc")
    suspend fun getPopularMovies(
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int
    ): Response<MovieResponse>
}
