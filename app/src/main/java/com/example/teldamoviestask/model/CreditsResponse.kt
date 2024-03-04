package com.example.teldamoviestask.model

data class CreditsResponse(
    val id: Int,
    val cast: List<CastMember>,
    val crew: List<CrewMember>
)

data class CastMember(
    val id: Int,
    val known_for_department: String,
    val name: String,
    val original_name: String,
    val popularity: Double,
    val profile_path: String?,
    val order: Int
)

data class CrewMember(
    val id: Int,
    val known_for_department: String,
    val name: String,
    val original_name: String,
    val popularity: Double,
    val profile_path: String?,
    val department: String,
    val job: String
)
