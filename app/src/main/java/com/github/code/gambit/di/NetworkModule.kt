package com.github.code.gambit.di

import com.github.code.gambit.data.mapper.network.FileNetworkMapper
import com.github.code.gambit.data.mapper.network.UrlNetworkMapper
import com.github.code.gambit.data.mapper.network.UserNetworkMapper
import com.github.code.gambit.data.remote.NetworkDataSource
import com.github.code.gambit.data.remote.NetworkDataSourceImpl
import com.github.code.gambit.data.remote.services.ApiService
import com.github.code.gambit.data.remote.services.file.FileService
import com.github.code.gambit.data.remote.services.file.FileServiceImpl
import com.github.code.gambit.data.remote.services.url.UrlService
import com.github.code.gambit.data.remote.services.url.UrlServiceImpl
import com.github.code.gambit.data.remote.services.user.UserService
import com.github.code.gambit.data.remote.services.user.UserServiceImpl
import com.github.code.gambit.utility.AppConstant
import com.github.code.gambit.utility.sharedpreference.UserManager
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
    fun provideRetroFitBuilder(gson: Gson, @Named(AppConstant.Named.BASE_URL) baseUrl: String): Retrofit.Builder {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun provideApiService(retrofitBuilder: Retrofit.Builder): ApiService {
        return retrofitBuilder.build().create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideFileService(apiService: ApiService, userManager: UserManager): FileService {
        return FileServiceImpl(apiService, userManager)
    }

    @Singleton
    @Provides
    fun provideUrlService(apiService: ApiService): UrlService {
        return UrlServiceImpl(apiService)
    }

    @Singleton
    @Provides
    fun provideUserService(apiService: ApiService, userManager: UserManager): UserService {
        return UserServiceImpl(apiService, userManager)
    }

    @Singleton
    @Provides
    fun provideNetworkDataSource(
        fileService: FileService,
        urlService: UrlService,
        userService: UserService,
        fileNetworkMapper: FileNetworkMapper,
        urlNetworkMapper: UrlNetworkMapper,
        userNetworkMapper: UserNetworkMapper
    ): NetworkDataSource {
        return NetworkDataSourceImpl(fileService, urlService, userService, fileNetworkMapper, urlNetworkMapper, userNetworkMapper)
    }

    @Singleton
    @Named(AppConstant.Named.BASE_URL)
    @Provides
    fun providesBaseUrl(): String {
        return "https://mhv71te0rh.execute-api.ap-south-1.amazonaws.com/beta/"
    }
}
