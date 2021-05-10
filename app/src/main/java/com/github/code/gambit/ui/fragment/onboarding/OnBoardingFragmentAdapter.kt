package com.github.code.gambit.ui.fragment.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.qualifiers.ApplicationContext

class OnBoardingFragmentAdapter(
    private val fragments: List<Fragment>,
    fragmentManager: FragmentManager,
    @ApplicationContext lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
