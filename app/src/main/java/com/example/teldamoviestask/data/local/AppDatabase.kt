package com.example.teldamoviestask.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.teldamoviestask.model.FavoriteItem

@Database(entities = [FavoriteItem::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteItemDao(): FavoriteItemDao
}