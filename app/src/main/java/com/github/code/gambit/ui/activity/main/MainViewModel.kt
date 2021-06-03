package com.github.code.gambit.ui.activity.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.github.code.gambit.backgroundtask.FileUploadWorker
import com.github.code.gambit.data.model.FileMetaData
import com.github.code.gambit.data.model.FileUploadStatus
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.helper.file.FileUploadState
import com.github.code.gambit.repositories.fileupload.FileUploadRepository
import com.github.code.gambit.utility.AppConstant
import com.github.code.gambit.utility.FileUtil.isSchemeTypeFile
import com.github.code.gambit.utility.SystemManager
import com.github.code.gambit.utility.sharedpreference.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val fileUploadRepository: FileUploadRepository
) : ViewModel() {

    private val _fileUploadState = MutableLiveData<FileUploadState>()
    val fileUploadState get() = _fileUploadState

    private lateinit var observer: Observer<List<WorkInfo>>
    private lateinit var workInfoLiveData: LiveData<List<WorkInfo>>
    private val uuids = mutableListOf<String>()

    @Inject
    lateinit var systemManager: SystemManager

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var workManager: WorkManager

    fun setEvent(event: MainEvent) {
        viewModelScope.launch {
            when (event) {
                is MainEvent.UploadFileEvent -> {
                    uploadFile(event.uri)
                }
                MainEvent.ObserveFileStatus -> observeWorkInfo()
            }
        }
    }

    private fun observeWorkInfo() {
        observer = Observer<List<WorkInfo>> { workInfos ->
            viewModelScope.launch {
                handleWorkInfo(workInfos)
            }
        }
        workInfoLiveData = workManager.getWorkInfosByTagLiveData(AppConstant.Worker.FILE_UPLOAD_TAG)
        workInfoLiveData.observeForever(observer)
    }

    private suspend fun handleWorkInfo(workInfos: List<WorkInfo>) {
        workInfos.forEach { workInfo ->
            val uuid = workInfo.id.toString()
            Timber.tag("work").i("$uuid ${workInfo.state}")
            if (!uuids.contains(uuid)) {
                val fmd = fileUploadRepository.getFileMetaData(uuid)
                if (fmd is ServiceResult.Success) {
                    fmd.data.uuid = uuid
                    val fileUploadStatus = FileUploadStatus(fmd.data, workInfo.state)
                    postStatus(fileUploadStatus)
                    uuids.add(uuid)
                }
            } else {
                uuids.find { it == uuid }?.let { it1 -> postStatus(it1, workInfo.state) }
            }
        }
    }

    private fun postStatus(uuid: String, state: WorkInfo.State) {
        _fileUploadState.postValue(FileUploadState.UpdateFileState(uuid, state))
    }

    private fun postStatus(fileUploadStatus: FileUploadStatus) {
        _fileUploadState.postValue(FileUploadState.NewFileUpload(fileUploadStatus))
    }

    private fun getFileMetaData(uri: Uri): FileMetaData {
        val path = systemManager.getFilePath(uri)
        val fileName: String
        val fileSize: Int
        if (uri.isSchemeTypeFile()) {
            val a = File(path)
            fileName = a.name
            fileSize = a.length().toInt()
        } else {
            fileSize = systemManager.getFileSize(uri)
            fileName = systemManager.getFileName(uri)
        }
        return FileMetaData(path, fileName, fileSize)
    }

    private fun uploadFile(uri: Uri) {
        val fileMetaData = getFileMetaData(uri)
        val request = OneTimeWorkRequestBuilder<FileUploadWorker>()
            .addTag(AppConstant.Worker.FILE_UPLOAD_TAG)
            .setInputData(createInputDataForUri(fileMetaData)).build()
        // todo enqueue unique work by passing unique string
        workManager.enqueue(request)
        /*workManager.getWorkInfoByIdLiveData(request.id).observeForever { workInfo: WorkInfo ->
            Timber.tag("work").i("info by id: ${workInfo.id}")
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
        }*/
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

    override fun onCleared() {
        if (this::workInfoLiveData.isInitialized) {
            if (this::observer.isInitialized) {
                workInfoLiveData.removeObserver(observer)
            }
        }
        super.onCleared()
    }
}

sealed class MainEvent {
    data class UploadFileEvent(val uri: Uri) : MainEvent()
    object ObserveFileStatus : MainEvent()
}
