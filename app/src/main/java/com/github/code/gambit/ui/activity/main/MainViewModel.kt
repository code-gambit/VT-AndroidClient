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
import java.lang.Exception
import java.util.Calendar
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

    private suspend fun observeWorkInfo() {
        when (val fmds = fileUploadRepository.getAllFileMetaData()) {
            is ServiceResult.Error -> {
                postError(fmds.exception)
                return
            }
            is ServiceResult.Success -> {
                fmds.data.forEach {
                    postFileMetaData(it)
                }
            }
        }
        observer = Observer<List<WorkInfo>> { workInfos ->
            workInfos.forEach { workInfo ->
                _fileUploadState.value = FileUploadState.UpdateFileState(workInfo.id.toString(), workInfo.state)
                Timber.tag("work").i("${workInfo.id} ${workInfo.state}")
            }
        }
        workInfoLiveData = workManager.getWorkInfosByTagLiveData(AppConstant.Worker.FILE_UPLOAD_TAG)
        workInfoLiveData.observeForever(observer)
    }

    private suspend fun uploadFile(uri: Uri) {
        val fileMetaData = getFileMetaData(uri)
        val request = OneTimeWorkRequestBuilder<FileUploadWorker>()
            .addTag(AppConstant.Worker.FILE_UPLOAD_TAG)
            .setInputData(createInputDataForUri(fileMetaData)).build()
        fileMetaData.uuid = request.id.toString()
        Timber.tag("work").i("${fileMetaData.uuid} enqueued")
        when (val res = fileUploadRepository.insertFileMetaData(fileMetaData)) {
            is ServiceResult.Error -> postError(res.exception)
            is ServiceResult.Success -> {
                postFileMetaData(fileMetaData)
                workManager.enqueue(request)
            }
        }
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
        return FileMetaData(path, fileName, fileSize, Calendar.getInstance().timeInMillis, "")
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

    private fun postFileMetaData(fileMetaData: FileMetaData) {
        Timber.tag("work").i("$fileMetaData from db")
        postStatus(FileUploadStatus(fileMetaData, WorkInfo.State.ENQUEUED))
    }

    private fun postStatus(fileUploadStatus: FileUploadStatus) {
        _fileUploadState.value = FileUploadState.NewFileUpload(fileUploadStatus)
    }

    private fun postError(exception: Exception) {
        _fileUploadState.postValue(FileUploadState.Error(exception.message!!))
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
