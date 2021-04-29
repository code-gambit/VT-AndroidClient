package com.github.code.gambit.di

import com.github.code.gambit.PreferenceManager
import com.github.code.gambit.network.api.ApiService
import com.github.code.gambit.network.api.file.FileService
import com.github.code.gambit.network.api.file.FileServiceImpl
import com.github.code.gambit.network.api.url.UrlService
import com.github.code.gambit.network.api.url.UrlServiceImpl
import com.github.code.gambit.network.api.user.UserService
import com.github.code.gambit.network.api.user.UserServiceImpl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

    @Singleton
    @Provides
    fun provideRetroFitBuilder(gson: Gson): Retrofit.Builder {
        return Retrofit.Builder().baseUrl("https://open-api.xyz/placeholder/")
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun provideApiService(retrofitBuilder: Retrofit.Builder): ApiService {
        return retrofitBuilder.build().create(ApiService::class.java)
    }

    @Singleton
    @Named("UID")
    @Provides
    fun provideUserId(preferenceManager: PreferenceManager): String {
        return preferenceManager.getUserId()!!
    }

    @Singleton
    @Provides
    fun provideFileService(apiService: ApiService): FileService {
        return FileServiceImpl(apiService)
    }

    @Singleton
    @Provides
    fun provideUrlService(apiService: ApiService): UrlService {
        return UrlServiceImpl(apiService)
    }

    @Singleton
    @Provides
    fun provideUserService(apiService: ApiService, @Named("UID") userId: String): UserService {
        return UserServiceImpl(apiService, userId)
    }
}
