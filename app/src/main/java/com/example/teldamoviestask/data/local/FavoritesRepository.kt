package com.example.teldamoviestask.data.local

import com.example.teldamoviestask.model.FavoriteItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoritesRepository @Inject constructor(private val favoriteItemDao: FavoriteItemDao) {

     suspend fun getAllFavorites(): List<FavoriteItem> = withContext(Dispatchers.IO) {
         val result = favoriteItemDao.getAllFavorites()
         result
     }

     suspend fun toggleFavorite(movieId: Int, isFavorite: Boolean) {
         withContext(Dispatchers.IO) {
             if (isFavorite) {
                 favoriteItemDao.deleteById(movieId)
             } else {
                 favoriteItemDao.insert(FavoriteItem(movieId))
             }
         }
    }
}
