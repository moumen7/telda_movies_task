package com.example.teldamoviestask.ui.movies_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teldamoviestask.data.local.FavoritesRepository
import com.example.teldamoviestask.data.remote.MoviesRepository
import com.example.teldamoviestask.model.Movie
import com.example.teldamoviestask.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesListViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _movies = MutableStateFlow<Resource<List<Movie>>>(Resource.Loading())
    val movies: StateFlow<Resource<List<Movie>>> = _movies.asStateFlow()

    private val _mostPopularMovies = MutableStateFlow<Resource<List<Movie>>>(Resource.Loading())
    private val _searchResultsMovies = MutableStateFlow<Resource<List<Movie>>>(Resource.Loading())

    private val _favorites = MutableLiveData<MutableSet<Int>>()
    val favorites = _favorites

    fun getFavorites() {
        viewModelScope.launch {
            val favoritesResult = favoritesRepository.getAllFavorites().mapNotNull { it.itemId }
                .toMutableSet()
            _favorites.postValue(favoritesResult)
        }
    }

    fun getPopularMovies() {
        viewModelScope.launch {
            _mostPopularMovies.value = Resource.Loading()
            val result = moviesRepository.getPopularMovies()
            if (result is Resource.Success) {
                _mostPopularMovies.value = Resource.Success(result.data?.results ?: emptyList())
            } else if (result is Resource.Error) {
                _mostPopularMovies.value = Resource.Error(result.message ?: "Unknown Error")
            }
            _movies.value = _mostPopularMovies.value
        }
    }

    fun toggleFavorite(movieId: Int, isFavorite: Boolean) = viewModelScope.launch {
        favoritesRepository.toggleFavorite(movieId, isFavorite)
        if (isFavorite) {
            // Create a new set with the removed favorite
            val updatedFavorites = _favorites.value?.toMutableSet() ?: mutableSetOf()
            updatedFavorites.remove(movieId)
            _favorites.value = updatedFavorites
        } else {
            // Create a new set without the additional favorite
            val updatedFavorites = _favorites.value?.toMutableSet() ?: mutableSetOf()
            updatedFavorites.add(movieId)
            _favorites.value = updatedFavorites
        }

    }

    suspend fun getMoviesbySearchTerm(searchTerm: String) {
        viewModelScope.launch {
            _searchResultsMovies.value = Resource.Loading()
            val result = moviesRepository.getMoviesbySearchTerm(searchTerm)

            if (result is Resource.Success) {
                _searchResultsMovies.value = Resource.Success(result.data?.results ?: emptyList())
            } else if (result is Resource.Error) {
                _searchResultsMovies.value = Resource.Error(result.message ?: "Unknown Error")
            }
            if (searchTerm.isNotEmpty())
                _movies.value = _searchResultsMovies.value
            else
                _movies.value = _mostPopularMovies.value

        }
    }
}