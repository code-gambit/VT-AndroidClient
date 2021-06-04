package com.github.code.gambit.di

import android.Manifest
import android.content.Context
import androidx.work.WorkManager
import com.cloudinary.android.MediaManager
import com.github.code.gambit.VTransfer
import com.github.code.gambit.ui.activity.main.FileMetaDataAdapter
import com.github.code.gambit.ui.fragment.home.FileListAdapter
import com.github.code.gambit.ui.fragment.onboarding.infoscreens.FirstOnBoardingFragment
import com.github.code.gambit.ui.fragment.onboarding.infoscreens.SecondOnBoardingFragment
import com.github.code.gambit.ui.fragment.onboarding.infoscreens.ThirdOnBoardingFragment
import com.github.code.gambit.utility.AppConstant
import com.github.code.gambit.utility.SystemManager
import com.github.code.gambit.utility.sharedpreference.LastEvaluatedKeyManager
import com.github.code.gambit.utility.sharedpreference.UserManager
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
    fun provideUserManager(@ApplicationContext context: Context): UserManager {
        return UserManager(context)
    }

    @Singleton
    @Provides
    fun provideLastEvaluatedKeyManager(@ApplicationContext context: Context): LastEvaluatedKeyManager {
        return LastEvaluatedKeyManager(context)
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
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
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

    @Provides
    fun provideFileListAdapter(@ApplicationContext context: Context): FileListAdapter {
        return FileListAdapter(context)
    }

    @Provides
    fun provideFileMetaDataAdapter(@ApplicationContext context: Context): FileMetaDataAdapter {
        return FileMetaDataAdapter(context)
    }
}
