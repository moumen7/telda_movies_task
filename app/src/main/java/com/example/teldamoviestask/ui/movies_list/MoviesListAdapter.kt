package com.example.teldamoviestask.ui.movies_list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.teldamoviestask.R
import com.example.teldamoviestask.data.constants.Constants
import com.example.teldamoviestask.databinding.MovieItemBinding
import com.example.teldamoviestask.model.Movie

class MoviesListAdapter(
    private val movies: List<Movie>,
    private var favorites: Set<Int>,
    private val onFavoriteToggle: (Int, Boolean) -> Unit,
    private val onItemClick: (Int,ImageView, Boolean) -> Unit
) : RecyclerView.Adapter<MoviesListAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<MovieItemBinding>(
            inflater, R.layout.movie_item, parent, false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        val isFavorite = favorites.contains(movie.id)
        holder.bind(movie, isFavorite, onFavoriteToggle, onItemClick)
    }

    override fun getItemCount(): Int = movies.size

    fun updateFavorites(favorites: Set<Int>) {
        this.favorites = favorites
        notifyDataSetChanged()
    }

    class MovieViewHolder(private val binding: MovieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            movie: Movie,
            isFavorite: Boolean,
            onFavoriteToggle: (Int, Boolean) -> Unit,
            onItemClick: (Int, ImageView, Boolean) -> Unit
        ) {
            binding.addToFavorites.setImageResource(if (isFavorite) R.drawable.heart_on else R.drawable.heart_off)
            binding.addToFavorites.setOnClickListener {
                movie.id?.let { it1 -> onFavoriteToggle(it1, isFavorite) }
            }

            binding.root.setOnClickListener {
                movie.id?.let { it1 -> onItemClick(it1, binding.movieImage, isFavorite) }
            }

            binding.movie = movie
            // Use Glide to load the movie image from URL
            Glide.with(binding.movieImage.context)
                .load(Constants.BASE_IMAGE_URL + movie.backdrop_path)
                .into(binding.movieImage)

            binding.executePendingBindings()
        }
    }
}
