package com.example.teldamoviestask.data.local

import androidx.lifecycle.LiveData
import com.example.teldamoviestask.model.FavoriteItem
import javax.inject.Inject

class FavoritesRepository @Inject constructor(private val favoriteItemDao: FavoriteItemDao) {

     fun getFavorites(): List<FavoriteItem> {

         return favoriteItemDao.getAllFavorites()
     }

     suspend fun toggleFavorite(movieId: Int, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteItemDao.deleteById(movieId)
        } else {
            favoriteItemDao.insert(FavoriteItem(movieId))
        }
    }
}
