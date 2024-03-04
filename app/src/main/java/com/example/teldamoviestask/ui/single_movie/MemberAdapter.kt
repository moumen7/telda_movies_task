package com.example.teldamoviestask.ui.single_movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.teldamoviestask.R
import com.example.teldamoviestask.data.constants.Constants
import com.example.teldamoviestask.databinding.MemberItemBinding

class MemberAdapter(
    private val similarMovies: List<String?>
    ) : RecyclerView.Adapter<MemberAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<MemberItemBinding>(
            inflater, R.layout.member_item, parent, false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val url = similarMovies[position]
        if (url != null) {
            holder.bind(url)
        }
    }

    override fun getItemCount(): Int = similarMovies.size



    class MovieViewHolder(private val binding: MemberItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            imageUrl: String
        ) {
            val fullImageUrl = Constants.BASE_IMAGE_URL + imageUrl
            // Use Glide to load the movie image from URL
            Glide.with(binding.movieImage.context)
                .load(fullImageUrl)
                .into(binding.movieImage)

            binding.executePendingBindings()
        }
    }
}
