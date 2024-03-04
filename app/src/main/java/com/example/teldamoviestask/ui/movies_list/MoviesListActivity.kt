package com.example.teldamoviestask.ui.movies_list

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teldamoviestask.R
import com.example.teldamoviestask.data.constants.Constants
import com.example.teldamoviestask.databinding.ActivityMoviesListBinding
import com.example.teldamoviestask.model.Movie
import com.example.teldamoviestask.model.Resource
import com.example.teldamoviestask.ui.single_movie.SingleMovieActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MoviesListActivity : AppCompatActivity() {
    private val viewModel: MoviesListViewModel by viewModels()
    private lateinit var binding: ActivityMoviesListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_list)
        binding = ActivityMoviesListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initEditText()
        initObservors()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun initObservors() {
        viewModel.favorites.observe(this, Observer { favoriteEntities ->
            (binding.moviesList.adapter as? MoviesListAdapter)?.updateFavorites(favoriteEntities)
        })
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movies.collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            binding.moviesListLoading.visibility = View.GONE
                            resource.data?.let { movies ->
                                initRecyclerView(movies)
                            }
                        }
                        is Resource.Loading -> {
                            binding.moviesListLoading.visibility = View.VISIBLE
                        }
                        is Resource.Error -> {
                            binding.moviesListLoading.visibility = View.GONE
                            // Show error message
                            Toast.makeText(
                                this@MoviesListActivity,
                                resource.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun initRecyclerView(movies: List<Movie>) {
        binding.moviesList.layoutManager = LinearLayoutManager(this)
        binding.moviesList.adapter =
            viewModel.favorites.value?.let {
                MoviesListAdapter(movies, it, { movieId, isCurrentlyFavorite ->
                    viewModel.toggleFavorite(movieId, isCurrentlyFavorite)
                }, { movieId, isFavorite ->
                    // Handle item click
                    navigateToMovieDetails(movieId, isFavorite)
                })

            }
    }
    private fun initEditText() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lifecycleScope.launch {
                    viewModel.getMoviesbySearchTerm(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun navigateToMovieDetails(movieId:Int,isFavorite:Boolean){
        val intent = Intent(this, SingleMovieActivity::class.java).apply {
            putExtra(Constants.ID, movieId)
            putExtra(Constants.IS_FAVORITE, isFavorite)
        }
        startActivity(intent)
        overridePendingTransition(
            R.anim.slide_from_right,
            R.anim.stay_put
        )
    }
    private fun getData() {
        lifecycleScope.launch {
            viewModel.getFavorites()
            viewModel.getPopularMovies()
        }
    }


}