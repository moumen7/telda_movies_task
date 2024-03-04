package com.example.teldamoviestask.data.remote
import com.example.teldamoviestask.BuildConfig
import com.example.teldamoviestask.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MoviesRepository @Inject constructor(
    private val movieApiService: MovieApiService,
    private val searchApi: SearchApi
) {
    suspend fun getPopularMovies(): Resource<MovieResponse> = withContext(Dispatchers.IO) {
        try {
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

    suspend fun getCasts(id: Int): Resource<CreditsResponse> = withContext(Dispatchers.IO) {
        try {
            val authHeader: String = "Bearer ${BuildConfig.API_KEY}"
            val response = movieApiService.getMovieCredits(id.toString(), authHeader, 1)

            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }

        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    suspend fun getSimilarMovies(id: Int): Resource<MovieResponse> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer ${BuildConfig.API_KEY}"
            val response = movieApiService.getSimilarMovies(id.toString(), authHeader,1)

            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }

        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    suspend fun getMovieDetails(id: Int): Resource<MovieDetails> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer ${BuildConfig.API_KEY}"
            val response = movieApiService.getMovieDetails(id.toString(), authHeader, 1)

            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }

        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    suspend fun getMoviesbySearchTerm(searchTerm: String): Resource<MovieResponse> = withContext(Dispatchers.IO) {
        try {
            val authHeader = "Bearer ${BuildConfig.API_KEY}"
            val response = searchApi.getMoviesbySearchTerm(authHeader,1, searchTerm)

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


