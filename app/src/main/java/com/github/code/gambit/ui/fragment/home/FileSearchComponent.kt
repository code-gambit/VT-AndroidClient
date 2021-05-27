package com.github.code.gambit.ui.fragment.home

import androidx.lifecycle.MutableLiveData
import com.github.code.gambit.data.model.File

interface FileSearchComponent {
    fun setRefreshing()
    fun setFileLoaded(files: List<File>)
    fun getRequests(): MutableLiveData<String>
    fun show()
}
