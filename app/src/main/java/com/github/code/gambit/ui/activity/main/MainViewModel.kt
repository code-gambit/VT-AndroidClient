package com.github.code.gambit.ui.activity.main

import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.github.code.gambit.PreferenceManager
import com.github.code.gambit.VTransfer
import com.github.code.gambit.data.entity.network.FileNetworkEntity
import com.github.code.gambit.helper.file.FileUploadState
import com.github.code.gambit.network.FileUploadWorker
import com.github.code.gambit.utility.AppConstant
import com.github.code.gambit.utility.SystemManager
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
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
    lateinit var preferenceManager: PreferenceManager

    fun setEvent(event: MainEvent) {
        when (event) {
            is MainEvent.UploadFileEvent -> {
                uploadFile(event.uri)
            }
        }
    }

    private fun uploadFile(uri: Uri) {
        val request = OneTimeWorkRequestBuilder<FileUploadWorker>()
            .setInputData(createInputDataForUri(uri)).build()
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
                _fileUploadState.postValue(FileUploadState.UploadStarted(systemManager.getFileName(getApplication(), uri)))
            }
        }
    }

    private fun createInputDataForUri(uri: Uri): Data {
        val builder = Data.Builder()
        val path = systemManager.getFilePath(getApplication(), uri)
        val fileName = systemManager.getFileName(getApplication(), uri)
        val fileSize = systemManager.getFileSize(getApplication(), uri)
        val userId = preferenceManager.getUserId()
        builder.let {
            it.putString(AppConstant.Worker.USER_ID, userId)
            it.putString(AppConstant.Worker.FILE_NAME_KEY, fileName)
            it.putString(AppConstant.Worker.FILE_URI_KEY, path)
            it.putInt(AppConstant.Worker.FILE_SIZE_KEY, fileSize)
        }
        return builder.build()
    }
}

sealed class MainEvent {
    data class UploadFileEvent(val uri: Uri) : MainEvent()
}
