package com.github.code.gambit.di

import com.cloudinary.android.MediaManager
import com.github.code.gambit.PreferenceManager
import com.github.code.gambit.data.mapper.aws.UserAttributeMapper
import com.github.code.gambit.network.auth.AuthService
import com.github.code.gambit.network.auth.AuthServiceImpl
import com.github.code.gambit.network.image.ImageService
import com.github.code.gambit.network.image.ImageServiceImpl
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
        return AuthServiceImpl()
    }

    @Singleton
    @Provides
    fun provideImageService(mediaManager: MediaManager): ImageService {
        return ImageServiceImpl(mediaManager)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        authService: AuthService,
        imageService: ImageService,
        preferenceManager: PreferenceManager,
        userAttributeMapper: UserAttributeMapper
    ): AuthRepository {
        return AuthRepository(authService, imageService, preferenceManager, userAttributeMapper)
    }
}
