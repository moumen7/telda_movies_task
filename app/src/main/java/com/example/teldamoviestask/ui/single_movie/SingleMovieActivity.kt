package com.example.teldamoviestask.ui.single_movie

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.teldamoviestask.R
import com.example.teldamoviestask.data.constants.Constants
import com.example.teldamoviestask.databinding.ActivitySingleMovieBinding
import com.example.teldamoviestask.model.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SingleMovieActivity : AppCompatActivity() {
    private val viewModel: SingleMovieViewModel by viewModels()
    private lateinit var binding: ActivitySingleMovieBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_movie)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        takeFullScreen()
        initViews()
        startFlow()
        setObservers()
    }

    private fun setObservers() {
        observeIsFavorite()
        observeSimilarMovies()
        observeActors()
        observeDirectors()
        observeMovieDetails()
    }

    private fun observeMovieDetails() {
        viewModel.movie.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Success -> {

                    Glide.with(binding.movieImage.context)
                        .load(Constants.BASE_IMAGE_URL + resource.data?.backdrop_path)
                        .into(binding.movieImage)
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                    // Show error message
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun observeSimilarMovies() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.similarMovies.collectLatest { similarMovies ->
                    when (similarMovies) {
                        is Resource.Success -> {
                            binding.similarMoviesLoading.visibility = View.GONE
                            // Update UI with the data
                            similarMovies.data?.let { movies ->
                                binding.similarMovies.adapter =
                                    SimilarMoviesAdapter(movies) { movieId ->
                                        // Handle item click
                                        navigateToMovieDetails(movieId)
                                    }
                            }
                        }
                        is Resource.Loading -> {
                            binding.similarMoviesLoading.visibility = View.VISIBLE
                        }
                        is Resource.Error -> {
                            binding.similarMoviesLoading.visibility = View.GONE
                            // Show error message
                            Toast.makeText(
                                this@SingleMovieActivity,
                                similarMovies.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToMovieDetails(movieId: Int) {
        val intent =
            Intent(
                this@SingleMovieActivity,
                SingleMovieActivity::class.java
            ).apply {
                putExtra(Constants.ID, movieId)
            }
        startActivity(intent)
    }

    private fun observeDirectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.directors.collectLatest { directors ->
                    when (directors) {
                        is Resource.Success -> {
                            binding.directors.adapter = directors.data?.map { it.profile_path }
                                ?.let { MemberAdapter(it) }
                        }
                        is Resource.Loading -> {
                        }
                        is Resource.Error -> {
                            // Show error message
                            Toast.makeText(
                                this@SingleMovieActivity,
                                directors.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            }
        }
    }

    private fun observeActors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actors.collectLatest { actors ->
                    when (actors) {
                        is Resource.Success -> {
                            binding.actorsLoading.visibility = View.GONE
                            binding.actors.adapter = actors.data?.map { it.profile_path }
                                ?.let { MemberAdapter(it) }
                        }
                        is Resource.Loading -> {
                            binding.actorsLoading.visibility = View.VISIBLE
                        }
                        is Resource.Error -> {
                            binding.actorsLoading.visibility = View.GONE
                            // Show error message
                            Toast.makeText(
                                this@SingleMovieActivity,
                                actors.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            }
        }
    }

    private fun observeIsFavorite() {
        lifecycleScope.launch {
            viewModel.isFavorite.collectLatest {isFavorite ->
                binding.addToFavorites.setImageResource(if (isFavorite) R.drawable.heart_on else R.drawable.heart_off)
            }
        }
    }

    private fun startFlow() {
        lifecycleScope.launch {
            viewModel.setIsFavorite(intent.getBooleanExtra(Constants.IS_FAVORITE, false))
            viewModel.getMovieDetails(intent.getIntExtra(Constants.ID, 0))
            viewModel.getSimilarMovies(intent.getIntExtra(Constants.ID, 0))
        }
    }

    private fun initViews() {
        binding.similarMovies.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.actors.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.directors.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.back.setOnClickListener {
            onBackPressed()
        }
        binding.addToFavorites.setOnClickListener {
            viewModel.toggleFavorite(intent.getIntExtra(Constants.ID, 0))
        }
    }

    private fun takeFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.stay_put, R.anim.slide_out_right)
    }
}