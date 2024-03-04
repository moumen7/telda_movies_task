package com.example.teldamoviestask.data.remote

import com.example.teldamoviestask.model.CreditsResponse
import com.example.teldamoviestask.model.MovieDetails
import com.example.teldamoviestask.model.MovieResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("discover/movie?include_adult=false&include_video=false&language=en-US&sort_by=popularity.desc")
    suspend fun getPopularMovies(
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int
    ): Response<MovieResponse>

    @GET("movie/{movie_id}?language=en-US")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: String,
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int
    ): Response<MovieDetails>
    @GET("movie/{movie_id}/similar?language=en-US&page=1")
    suspend fun getSimilarMovies(
        @Path("movie_id") movieId: String,
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int
    ): Response<MovieResponse>

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: String,
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int
    ): Response<CreditsResponse>

}
