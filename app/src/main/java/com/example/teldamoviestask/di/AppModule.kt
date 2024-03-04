package com.example.teldamoviestask.di

import android.content.Context
import androidx.room.Room
import com.example.teldamoviestask.data.constants.Constants
import com.example.teldamoviestask.data.local.AppDatabase
import com.example.teldamoviestask.data.local.FavoriteItemDao
import com.example.teldamoviestask.data.remote.MovieApiService
import com.example.teldamoviestask.data.remote.SearchApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.base_url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    @Provides
    @Singleton
    fun provideMovieApiService(retrofit: Retrofit): MovieApiService = retrofit.create(
        MovieApiService::class.java)

    @Provides
    @Singleton
    fun provideSearchApi(retrofit: Retrofit): SearchApi = retrofit.create(
        SearchApi::class.java)


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, Constants.database_name).build()

    @Provides
    fun provideFavoriteItemDao(appDatabase: AppDatabase): FavoriteItemDao =
        appDatabase.favoriteItemDao()
}
