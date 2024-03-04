package com.example.teldamoviestask.data.remote

import com.example.teldamoviestask.model.MovieResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchApi {
    @GET("search/movie?include_adult=false&language=en-US&page=1")
    suspend fun getMoviesbySearchTerm(
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int,
        @Query("query") query: String
    ): Response<MovieResponse>
}
