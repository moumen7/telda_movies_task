package com.example.teldamoviestask.data.remote

import com.example.teldamoviestask.BuildConfig
import com.example.teldamoviestask.model.MovieResponse
import com.example.teldamoviestask.model.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MoviesRepository @Inject constructor(private val movieApiService: MovieApiService, private  val searchApi: SearchApi) {
    suspend fun getPopularMovies(): Resource<MovieResponse> {
        return try {
            val page = 1
            val authHeader: String = "Bearer ${BuildConfig.API_KEY}"
            val response = movieApiService.getPopularMovies(authHeader, page)

            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }

        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    suspend fun getMoviesbySearchTerm(searchTerm: String): Resource<MovieResponse> {
        return try {
            val page = 1
            val authHeader: String = "Bearer ${BuildConfig.API_KEY}"
            val response = searchApi.getMoviesbySearchTerm(authHeader, page, searchTerm)

            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }

        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }
}

