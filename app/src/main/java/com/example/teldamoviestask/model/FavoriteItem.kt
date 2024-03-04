package com.example.teldamoviestask.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.teldamoviestask.data.constants.Constants

@Entity(tableName = Constants.table_name)
data class FavoriteItem(@PrimaryKey val itemId: Int?)
