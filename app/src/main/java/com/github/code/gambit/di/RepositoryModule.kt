package com.github.code.gambit.di

import com.github.code.gambit.data.local.CacheDataSource
import com.github.code.gambit.data.mapper.aws.UserAttributeMapper
import com.github.code.gambit.data.remote.NetworkDataSource
import com.github.code.gambit.data.remote.services.auth.AuthService
import com.github.code.gambit.data.remote.services.auth.AuthServiceImpl
import com.github.code.gambit.repositories.AuthRepository
import com.github.code.gambit.repositories.HomeRepository
import com.github.code.gambit.repositories.HomeRepositoryImpl
import com.github.code.gambit.repositories.ProfileRepository
import com.github.code.gambit.repositories.ProfileRepositoryImpl
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
    fun provideAuthService(): AuthService {
        return AuthServiceImpl()
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        authService: AuthService,
        networkDataSource: NetworkDataSource,
        userManager: UserManager,
        userAttributeMapper: UserAttributeMapper
    ): AuthRepository {
        return AuthRepository(authService, networkDataSource, userManager, userAttributeMapper)
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
}
