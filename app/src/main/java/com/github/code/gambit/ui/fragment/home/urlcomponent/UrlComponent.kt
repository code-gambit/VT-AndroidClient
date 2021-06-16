package com.github.code.gambit.ui.fragment.home.urlcomponent

import android.content.Context
import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url

interface UrlComponent {
    companion object {
        fun bind(
            context: Context,
            onCancel: () -> Unit = {},
            onUpdate: (url: Url) -> Unit,
            onDelete: (url: Url) -> Unit
        ): UrlComponent {
            return UrlComponentImpl(context).apply {
                this.addOnCancelCallback(onCancel)
                this.addOnUpdateCallback(onUpdate)
                this.addOnDeleteCallback(onDelete)
            }
        }
    }

    fun show(url: Url, file: File)
    fun hide()
}
