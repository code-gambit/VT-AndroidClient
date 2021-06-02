package com.github.code.gambit.data.model

import androidx.work.WorkInfo

data class FileUploadStatus(
    var fileMetaData: FileMetaData,
    var state: WorkInfo.State
)
