package com.example.teldamoviestask.ui.single_movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teldamoviestask.data.local.FavoritesRepository
import com.example.teldamoviestask.data.remote.MoviesRepository
import com.example.teldamoviestask.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.example.teldamoviestask.data.constants.Constants

@HiltViewModel
class SingleMovieViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {


    private val _movie = MutableLiveData<Resource<MovieDetails>>(Resource.Loading())
    val movie: LiveData<Resource<MovieDetails>> = _movie

    private val _similarMovies = MutableStateFlow<Resource<List<Movie>>>(Resource.Loading())
    val similarMovies: StateFlow<Resource<List<Movie>>> = _similarMovies.asStateFlow()


    private val _actors = MutableStateFlow<Resource<List<CastMember>>>(Resource.Loading())
    val actors = _actors.asStateFlow()


    private val _directors = MutableStateFlow<Resource<List<CrewMember>>>(Resource.Loading())
    val directors = _directors.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()


    fun toggleFavorite(movieId: Int) = viewModelScope.launch {
        favoritesRepository.toggleFavorite(movieId, isFavorite.value)
        _isFavorite.value = !isFavorite.value
    }

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _movie.value = Resource.Loading()
            val result = moviesRepository.getMovieDetails(movieId)
            _movie.value = result
        }
    }

    fun getSimilarMovies(movieId: Int) {
        viewModelScope.launch {
            _similarMovies.value = Resource.Loading()
            val result = moviesRepository.getSimilarMovies(movieId)
            if (result is Resource.Success) {
                if(result.data!=null) {
                    _similarMovies.value = Resource.Success(result.data.results.take(5))
                    val credits =
                        result.data.results.map { movie ->
                            async { movie.id?.let { moviesRepository.getCasts(it).data } }
                        }.awaitAll()

                    _actors.value = Resource.Success(credits.flatMap { it?.cast ?: listOf() }
                        .filter { it.known_for_department == Constants.ACTOR }
                        .sortedByDescending { it.popularity }
                        .take(5))

                    _directors.value = Resource.Success(credits.flatMap { it?.crew ?: listOf() }
                        .filter { it.department == Constants.DIRECTOR }
                        .sortedByDescending { it.popularity }
                        .take(5))
                }
            } else if (result is Resource.Error) {
                _similarMovies.value = Resource.Error(result.message ?: "Unknown Error")
            }
        }
    }

    fun setIsFavorite(isFavorite: Boolean) {
        _isFavorite.value = isFavorite
    }
}