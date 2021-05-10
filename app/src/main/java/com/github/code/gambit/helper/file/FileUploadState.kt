package com.github.code.gambit.helper.file

import com.github.code.gambit.data.entity.network.FileNetworkEntity

sealed class FileUploadState {
    data class UploadStarted(val fileName: String) : FileUploadState()
    data class UploadSuccess(val file: FileNetworkEntity) : FileUploadState()
}
