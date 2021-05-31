package com.github.code.gambit.ui.fragment.home.searchcomponent

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.github.code.gambit.data.model.File
import com.github.code.gambit.databinding.SearchLayoutBinding
import com.github.code.gambit.ui.fragment.home.FileListAdapter

interface FileSearchComponent {
    companion object {
        fun bind(
            searchLayoutBinding: SearchLayoutBinding,
            adapter: FileListAdapter,
            context: Context,
            closeFunc: () -> Unit
        ): FileSearchComponent {
            return FileSearchComponentImpl(searchLayoutBinding, adapter).apply {
                registerComponents(context, closeFunc)
            }
        }
    }
    fun setRefreshing()
    fun setFileLoaded(files: List<File>)
    fun getRequests(): MutableLiveData<String>
    fun show()
}
