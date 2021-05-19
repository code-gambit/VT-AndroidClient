package com.github.code.gambit.ui.activity.main

import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.github.code.gambit.VTransfer
import com.github.code.gambit.backgroundtask.FileUploadWorker
import com.github.code.gambit.data.entity.network.FileNetworkEntity
import com.github.code.gambit.data.model.FileMetaData
import com.github.code.gambit.helper.file.FileUploadState
import com.github.code.gambit.utility.AppConstant
import com.github.code.gambit.utility.FileUtil.isSchemeTypeFile
import com.github.code.gambit.utility.SystemManager
import com.github.code.gambit.utility.sharedpreference.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    application: VTransfer
) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application.baseContext)

    private val _fileUploadState = MutableLiveData<FileUploadState>()

    val fileUploadState get() = _fileUploadState

    @Inject
    lateinit var systemManager: SystemManager

    @Inject
    lateinit var userManager: UserManager

    fun setEvent(event: MainEvent) {
        when (event) {
            is MainEvent.UploadFileEvent -> {
                uploadFile(event.uri)
            }
        }
    }

    private fun uploadFile(uri: Uri) {
        val fileMetaData = getFileMetaData(uri)
        val request = OneTimeWorkRequestBuilder<FileUploadWorker>()
            .setInputData(createInputDataForUri(fileMetaData)).build()
        workManager.enqueue(request)
        workManager.getWorkInfoByIdLiveData(request.id).observeForever { workInfo: WorkInfo ->
            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                val string = workInfo.outputData.getString(AppConstant.Worker.FILE_OUTPUT_KEY)
                if (string != null) {
                    val file = FileNetworkEntity.fromString(string)
                    _fileUploadState.postValue(FileUploadState.UploadSuccess(file))
                    Timber.tag("out").i(file.toString())
                }
            } else if (workInfo.state == WorkInfo.State.RUNNING) {
                _fileUploadState.postValue(FileUploadState.UploadStarted(fileMetaData.name))
            }
        }
    }

    private fun getFileMetaData(uri: Uri): FileMetaData {
        val path = systemManager.getFilePath(getApplication(), uri)
        val fileName: String
        val fileSize: Int
        if (uri.isSchemeTypeFile()) {
            val a = File(path)
            fileName = a.name
            fileSize = a.length().toInt()
        } else {
            fileSize = systemManager.getFileSize(getApplication(), uri)
            fileName = systemManager.getFileName(getApplication(), uri)
        }
        return FileMetaData(path, fileName, fileSize)
    }

    private fun createInputDataForUri(fileMetaData: FileMetaData): Data {
        val builder = Data.Builder()
        val userId = userManager.getUserId()
        builder.let {
            it.putString(AppConstant.Worker.USER_ID, userId)
            it.putString(AppConstant.Worker.FILE_META_DATA, fileMetaData.toString())
            it.putString(AppConstant.Worker.FILE_NAME_KEY, fileMetaData.name)
            it.putString(AppConstant.Worker.FILE_URI_KEY, fileMetaData.path)
            it.putInt(AppConstant.Worker.FILE_SIZE_KEY, fileMetaData.size)
        }
        return builder.build()
    }
}

sealed class MainEvent {
    data class UploadFileEvent(val uri: Uri) : MainEvent()
}
