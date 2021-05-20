package com.github.code.gambit.di

import android.content.Context
import androidx.room.Room
import com.github.code.gambit.data.local.CacheDataSource
import com.github.code.gambit.data.local.CacheDataSourceImpl
import com.github.code.gambit.data.local.Database
import com.github.code.gambit.data.local.FileDao
import com.github.code.gambit.data.mapper.cache.FileCacheMapper
import com.github.code.gambit.utility.AppConstant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(context, Database::class.java, AppConstant.Database.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideFileDao(database: Database): FileDao {
        return database.fileDao()
    }

    @Singleton
    @Provides
    fun provideCacheDataSource(
        fileCacheMapper: FileCacheMapper,
        fileDao: FileDao
    ): CacheDataSource {
        return CacheDataSourceImpl(fileCacheMapper, fileDao)
    }
}
