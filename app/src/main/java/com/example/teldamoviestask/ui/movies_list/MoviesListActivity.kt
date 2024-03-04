package com.example.teldamoviestask.ui.movies_list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teldamoviestask.R
import com.example.teldamoviestask.databinding.ActivityMainBinding
import com.example.teldamoviestask.model.Movie
import com.example.teldamoviestask.model.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MoviesListActivity : AppCompatActivity() {
    private val viewModel: MoviesListViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getData()
        initViews()
        initObservors()
    }

    private fun initObservors() {
        viewModel.favorites.observe(this, Observer { favoriteEntities ->
            (binding.moviesList.adapter as? MoviesListAdapter)?.updateFavorites(favoriteEntities)
        })
        viewModel.movies.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    // Update UI with the data
                    resource.data?.let { movies ->
                        initRecyclerView(movies)
                    }
                }
                is Resource.Loading -> {
                    // Show loading indicator
                }
                is Resource.Error -> {
                    // Show error message
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun initRecyclerView(movies: List<Movie>) {
        binding.moviesList.layoutManager = LinearLayoutManager(this)
        binding.moviesList.adapter =
            viewModel.favorites.value?.let {
                MoviesListAdapter(movies, it) { movieId, isCurrentlyFavorite ->
                    viewModel.toggleFavorite(movieId, isCurrentlyFavorite)
                }

            }
    }

    private fun getData() {
        lifecycleScope.launch {
            viewModel.fetchFavorites()
            viewModel.fetchPopularMovies()
        }
    }
    private fun initViews() {
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
}