package com.github.code.gambit.di

import com.github.code.gambit.data.local.CacheDataSource
import com.github.code.gambit.data.mapper.aws.UserAttributeMapper
import com.github.code.gambit.data.remote.NetworkDataSource
import com.github.code.gambit.data.remote.services.auth.AuthService
import com.github.code.gambit.repositories.auth.AuthRepository
import com.github.code.gambit.repositories.auth.AuthRepositoryImpl
import com.github.code.gambit.repositories.fileupload.FileUploadRepository
import com.github.code.gambit.repositories.fileupload.FileUploadRepositoryImpl
import com.github.code.gambit.repositories.home.HomeRepository
import com.github.code.gambit.repositories.home.HomeRepositoryImpl
import com.github.code.gambit.repositories.profile.ProfileRepository
import com.github.code.gambit.repositories.profile.ProfileRepositoryImpl
import com.github.code.gambit.utility.sharedpreference.LastEvaluatedKeyManager
import com.github.code.gambit.utility.sharedpreference.UserManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideUserAttributeMapper(): UserAttributeMapper {
        return UserAttributeMapper()
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        authService: AuthService,
        networkDataSource: NetworkDataSource,
        cacheDataSource: CacheDataSource,
        userManager: UserManager,
        lastEvaluatedKeyManager: LastEvaluatedKeyManager,
        userAttributeMapper: UserAttributeMapper
    ): AuthRepository {
        return AuthRepositoryImpl(
            authService,
            networkDataSource,
            cacheDataSource,
            userManager,
            lastEvaluatedKeyManager,
            userAttributeMapper
        )
    }

    @Singleton
    @Provides
    fun provideProfileRepository(
        networkDataSource: NetworkDataSource,
        authService: AuthService,
        userManager: UserManager
    ): ProfileRepository {
        return ProfileRepositoryImpl(networkDataSource, authService, userManager)
    }

    @Singleton
    @Provides
    fun provideHomeRepository(
        cacheDataSource: CacheDataSource,
        networkDataSource: NetworkDataSource
    ): HomeRepository {
        return HomeRepositoryImpl(cacheDataSource, networkDataSource)
    }

    @Singleton
    @Provides
    fun provideFileUploadRepository(cacheDataSource: CacheDataSource): FileUploadRepository {
        return FileUploadRepositoryImpl(cacheDataSource)
    }
}
