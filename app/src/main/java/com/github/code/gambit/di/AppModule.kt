package com.github.code.gambit.di

import android.Manifest
import android.content.Context
import com.cloudinary.android.MediaManager
import com.github.code.gambit.PreferenceManager
import com.github.code.gambit.VTransfer
import com.github.code.gambit.ui.fragment.onboarding.infoscreens.FirstOnBoardingFragment
import com.github.code.gambit.ui.fragment.onboarding.infoscreens.SecondOnBoardingFragment
import com.github.code.gambit.ui.fragment.onboarding.infoscreens.ThirdOnBoardingFragment
import com.github.code.gambit.utility.AppConstant
import com.github.code.gambit.utility.SystemManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideBaseApplication(@ApplicationContext context: Context): VTransfer {
        return context as VTransfer
    }

    @Singleton
    @Provides
    fun providePrefManager(@ApplicationContext context: Context): PreferenceManager {
        return PreferenceManager(context)
    }

    @Singleton
    @Provides
    fun getFirstFragment(): FirstOnBoardingFragment {
        return FirstOnBoardingFragment()
    }

    @Singleton
    @Provides
    fun getSecondFragment(): SecondOnBoardingFragment {
        return SecondOnBoardingFragment()
    }

    @Singleton
    @Provides
    fun getThirdFragment(): ThirdOnBoardingFragment {
        return ThirdOnBoardingFragment()
    }

    @Singleton
    @Provides
    fun provideMediaManager(): MediaManager {
        return MediaManager.get()
    }

    @Singleton
    @Provides
    @Named(AppConstant.Named.PERMISSION_ARRAY)
    fun providePermissionArray(): List<String> {
        return listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    @Singleton
    @Provides
    fun provideSystemManager(
        @ApplicationContext ctx: Context,
        @Named(AppConstant.Named.PERMISSION_ARRAY) permission: List<String>
    ): SystemManager {
        return SystemManager(ctx, permission)
    }
}
