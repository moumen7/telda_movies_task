package com.example.teldamoviestask.ui.movies_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teldamoviestask.data.local.FavoritesRepository
import com.example.teldamoviestask.data.remote.MoviesRepository
import com.example.teldamoviestask.model.Movie
import com.example.teldamoviestask.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MoviesListViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _movies = MutableLiveData<Resource<List<Movie>>>(Resource.Loading())
    val movies: LiveData<Resource<List<Movie>>> = _movies
    private val _mostPopularMovies = MutableLiveData<Resource<List<Movie>>>(Resource.Loading())
    private val _searchResultsMovies = MutableLiveData<Resource<List<Movie>>>(Resource.Loading())

    private val _favorites = MutableLiveData<MutableSet<Int>>()
    val favorites = _favorites

    fun fetchFavorites() {
        viewModelScope.launch {
            _favorites.postValue(favoritesRepository.getAllFavorites().mapNotNull { it.itemId }
                .toMutableSet())
        }
    }

    fun fetchPopularMovies() {
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
            // Create a new set with the additional favorite
            val updatedFavorites = _favorites.value?.toMutableSet() ?: mutableSetOf()
            updatedFavorites.remove(movieId)
            _favorites.value = updatedFavorites
        } else {
            // Create a new set without the removed favorite
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
            if (searchTerm.length > 0)
                _movies.value = _searchResultsMovies.value
            else
                _movies.value = _mostPopularMovies.value

        }
    }
}