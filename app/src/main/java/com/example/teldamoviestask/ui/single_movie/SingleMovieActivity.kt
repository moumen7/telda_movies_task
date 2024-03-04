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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }
        binding.back.setOnClickListener {
            onBackPressed()
        }
        lifecycleScope.launch {
            viewModel.setIsFavorite(intent.getBooleanExtra(Constants.IS_FAVORITE, false))
            viewModel.fetchMovieDetails(intent.getIntExtra(Constants.ID, 0))
            viewModel.fetchSimilarMovies(intent.getIntExtra(Constants.ID, 0))
        }
        binding.addToFavorites.setOnClickListener {
            viewModel.toggleFavorite(intent.getIntExtra(Constants.ID, 0))
        }
        lifecycleScope.launch {
            viewModel.isFavorite.collectLatest {
                binding.addToFavorites.setImageResource(if (it) R.drawable.heart_on else R.drawable.heart_off)
            }
        }
        binding.movieOverview.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (binding.movieOverview.lineCount > 5) {
                    val end =
                        binding.movieOverview.layout.getLineEnd(4) // Get the end position of the 5th line
                    val moreString = " Read More"
                    val displayText =
                        binding.movieOverview.text.subSequence(0, end) as String + moreString
                    binding.movieOverview.text = displayText

                    val spannableString = SpannableString(binding.movieOverview.text)
                    val clickableSpan: ClickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            // Expand the TextView to show all lines
                            binding.movieOverview.maxLines = Integer.MAX_VALUE
                            binding.movieOverview.text = viewModel.movie.value?.data?.overview
                        }
                    }

                    spannableString.setSpan(
                        clickableSpan,
                        displayText.length - moreString.length,
                        displayText.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    binding.movieOverview.text = spannableString
                    binding.movieOverview.movementMethod = LinkMovementMethod.getInstance()

                    // Remove the listener to prevent multiple triggers
                    binding.movieOverview.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })

        binding.similarMovies.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.actors.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.directors.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actors.collectLatest { actors ->
                    binding.actors.adapter = actors.data?.map { it.profile_path }
                        ?.let { MemberAdapter(it) }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.directors.collectLatest { directors ->
                    binding.directors.adapter = directors.data?.map { it.profile_path }
                        ?.let { MemberAdapter(it) }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.similarMovies.collectLatest { similarMovies ->
                    when (similarMovies) {
                        is Resource.Success -> {
                            // Update UI with the data
                            similarMovies.data?.let { movies ->
                                binding.similarMovies.adapter =
                                    SimilarMoviesAdapter(movies.results) { movieId ->
                                        // Handle item click
                                        val intent =
                                            Intent(
                                                this@SingleMovieActivity,
                                                SingleMovieActivity::class.java
                                            ).apply {
                                                putExtra("id", movieId)
                                            }
                                        startActivity(intent)
                                    }
                            }
                        }
                        is Resource.Loading -> {
                            // Show loading indicator
                        }
                        is Resource.Error -> {
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


        viewModel.movie.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    Glide.with(binding.movieImage.context)
                        .load(Constants.BASE_IMAGE_URL + resource.data?.backdrop_path)
                        .into(binding.movieImage)
                }
                is Resource.Loading -> {
                    Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    // Show error message
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}