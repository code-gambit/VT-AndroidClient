package com.github.code.gambit.di

import android.content.Context
import com.github.code.gambit.data.mapper.network.FileNetworkMapper
import com.github.code.gambit.data.mapper.network.UrlNetworkMapper
import com.github.code.gambit.data.mapper.network.UserNetworkMapper
import com.github.code.gambit.data.remote.ApiGatewayInterceptor
import com.github.code.gambit.data.remote.NetworkDataSource
import com.github.code.gambit.data.remote.NetworkDataSourceImpl
import com.github.code.gambit.data.remote.services.ApiService
import com.github.code.gambit.data.remote.services.NetworkInterceptor
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
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

    @Singleton
    @Provides
    fun provideApiGatewayInterceptor(): ApiGatewayInterceptor {
        return ApiGatewayInterceptor()
    }

    @Singleton
    @Provides
    fun provideNetworkInterceptor(
        @ApplicationContext context: Context
    ): NetworkInterceptor {
        return NetworkInterceptor(context)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        apiGatewayInterceptor: ApiGatewayInterceptor,
        networkInterceptor: NetworkInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(apiGatewayInterceptor)
            .addInterceptor(networkInterceptor).build()
    }

    @Singleton
    @Provides
    fun provideRetroFitBuilder(
        gson: Gson,
        client: OkHttpClient
    ): Retrofit.Builder {
        return Retrofit.Builder().baseUrl(AppConstant.BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun provideApiService(retrofitBuilder: Retrofit.Builder,): ApiService {
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
}
