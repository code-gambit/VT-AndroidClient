package com.github.code.gambit.di

import android.content.Context
import com.github.code.gambit.PreferenceManager
import com.github.code.gambit.ui.fragment.onboarding.infoscreens.FirstOnBoardingFragment
import com.github.code.gambit.ui.fragment.onboarding.infoscreens.SecondOnBoardingFragment
import com.github.code.gambit.ui.fragment.onboarding.infoscreens.ThirdOnBoardingFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePrefManager(@ApplicationContext context: Context): PreferenceManager {
        return PreferenceManager(context)
    }

    @Provides
    fun getFirstFragment(): FirstOnBoardingFragment {
        return FirstOnBoardingFragment()
    }

    @Provides
    fun getSecondFragment(): SecondOnBoardingFragment {
        return SecondOnBoardingFragment()
    }

    @Provides
    fun getThirdFragment(): ThirdOnBoardingFragment {
        return ThirdOnBoardingFragment()
    }
}
