package com.github.code.gambit.ui.fragment.home.filtercomponent

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.github.code.gambit.databinding.FilterLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

interface FilterComponent {
    companion object {
        fun bind(
            binding: FilterLayoutBinding,
            context: Context,
            supportFragmentManager: FragmentManager
        ): FilterComponent {
            return FilterComponentImpl(binding, context).apply {
                this.fragmentManager = supportFragmentManager
            }
        }
    }

    fun getFilterRequest(): MutableLiveData<Filter>
    fun addBottomSheetCallback(callback: BottomSheetBehavior.BottomSheetCallback)
    fun isFilterEnabled(): Boolean
    fun clearFilter()
    fun show()
    fun hide()
}
