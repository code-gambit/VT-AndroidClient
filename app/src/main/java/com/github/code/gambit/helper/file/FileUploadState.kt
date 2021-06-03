package com.github.code.gambit.helper.file

import androidx.work.WorkInfo
import com.github.code.gambit.data.entity.network.FileNetworkEntity
import com.github.code.gambit.data.model.FileUploadStatus

sealed class FileUploadState {
    data class UploadStarted(val fileName: String) : FileUploadState()
    data class UploadSuccess(val file: FileNetworkEntity) : FileUploadState()
    data class UpdateFileState(val uuid: String, val newState: WorkInfo.State) : FileUploadState()
    data class NewFileUpload(val fileUploadStatus: FileUploadStatus) : FileUploadState()
}
