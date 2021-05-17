package com.github.code.gambit.data.remote.services.image

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.utility.AppConstant
import java.lang.Exception
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ImageServiceImpl
constructor(val mediaManager: MediaManager) : ImageService {

    override suspend fun uploadImage(imageUri: Uri): ServiceResult<String> {
        return suspendCoroutine { cont: Continuation<ServiceResult<String>> ->
            val callback = object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val fileId =
                        (resultData?.get(AppConstant.Cloudinary.RESULT_URL_KEY) as String).let {
                            val arr = it.split("/")
                            arr[arr.size - 1]
                        }
                    cont.resume(ServiceResult.Success(fileId))
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    cont.resume(ServiceResult.Error(Exception(error?.description)))
                }
            }
            mediaManager.upload(imageUri).unsigned(AppConstant.Cloudinary.UPLOAD_PRESET)
                .callback(callback).dispatch()
        }
    }
}
