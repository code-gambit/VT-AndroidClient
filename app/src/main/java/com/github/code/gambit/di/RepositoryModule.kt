package com.github.code.gambit.di

import com.github.code.gambit.PreferenceManager
import com.github.code.gambit.data.mapper.aws.UserAttributeMapper
import com.github.code.gambit.network.auth.AuthService
import com.github.code.gambit.network.auth.AuthService_Impl
import com.github.code.gambit.repository.AuthRepository
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
        return AuthService_Impl()
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        authService: AuthService,
        preferenceManager: PreferenceManager,
        userAttributeMapper: UserAttributeMapper
    ): AuthRepository {
        return AuthRepository(authService, preferenceManager, userAttributeMapper)
    }
}
