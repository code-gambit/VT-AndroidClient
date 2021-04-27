package com.github.code.gambit.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.code.gambit.PreferenceManager
import com.github.code.gambit.R
import com.github.code.gambit.utility.exitFullscreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.exitFullscreen()
    }
}
