package com.github.code.gambit.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.github.code.gambit.PreferenceManager
import com.github.code.gambit.R
import com.github.code.gambit.utility.exitFullscreen
import dagger.hilt.android.AndroidEntryPoint
import io.ipfs.kotlin.defaults.InfuraIPFS
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.exitFullscreen()
        ipfsStart()
    }

    fun ipfsStart() {

        GlobalScope.launch {
            val a = InfuraIPFS().add.string("hello world").Hash
            Log.i("ipfs", "ipfsStart: $a")
        }
    }
}
