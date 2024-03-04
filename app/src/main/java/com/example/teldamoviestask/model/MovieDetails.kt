package com.example.teldamoviestask.model

data class MovieDetails(
    val id: Int,
    val original_title: String,
    val overview: String?,
    val popularity: Double,
    val poster_path: String?,
    val backdrop_path: String?,
    val release_date: String,
    val revenue: Int,
    val status: String,
    val tagline: String?,
    val title: String,
    val vote_average: Double,
)
