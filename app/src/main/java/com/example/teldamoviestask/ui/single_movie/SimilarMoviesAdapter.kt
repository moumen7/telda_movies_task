package com.example.teldamoviestask.ui.single_movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.teldamoviestask.R
import com.example.teldamoviestask.data.constants.Constants
import com.example.teldamoviestask.databinding.SimilarMovieItemBinding
import com.example.teldamoviestask.model.Movie

class SimilarMoviesAdapter(
    private val similarMovies: List<Movie>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SimilarMoviesAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<SimilarMovieItemBinding>(
            inflater, R.layout.similar_movie_item, parent, false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = similarMovies[position]
        holder.bind(movie,  onItemClick)
    }

    override fun getItemCount(): Int = similarMovies.size



    class MovieViewHolder(private val binding: SimilarMovieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            movie: Movie,
            onItemClick: (Int) -> Unit
        ) {

            binding.root.setOnClickListener {
                movie.id?.let { it1 -> onItemClick(it1) }
            }

            binding.movie = movie
            // Use Glide to load the movie image from URL
            Glide.with(binding.movieImage.context)
                .load(Constants.BASE_IMAGE_URL + movie.poster_path)
                .into(binding.movieImage)

            binding.executePendingBindings()
        }
    }
}
