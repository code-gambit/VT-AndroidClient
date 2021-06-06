package com.github.code.gambit.ui.fragment.auth

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class AuthFragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private var fragments: List<Fragment> = listOf(SignUpFragment(), LoginFragment())

    companion object {
        fun getInstance(fragmentManager: FragmentManager, lifecycle: Lifecycle): AuthFragmentAdapter {
            return AuthFragmentAdapter(fragmentManager, lifecycle)
        }
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun getFragment(pos: Int): Fragment {
        return fragments[pos]
    }
}
