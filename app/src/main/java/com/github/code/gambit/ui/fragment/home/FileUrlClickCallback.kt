package com.github.code.gambit.ui.fragment.home

import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.ui.OnItemClickListener

interface FileUrlClickCallback : OnItemClickListener<File> {
    fun onCreateNewUrl(file: File)
    fun onLoadMoreUrl(file: File)
    fun onUrlLongClick(url: Url, file: File)
    fun onUrlClick(url: Url, file: File)
}
