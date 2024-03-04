package com.example.teldamoviestask.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.teldamoviestask.model.FavoriteItem


@Dao
interface FavoriteItemDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): List<FavoriteItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteItem: FavoriteItem)

    @Query("DELETE FROM favorites WHERE itemId = :itemId")
    suspend fun deleteById(itemId: Int)
}
