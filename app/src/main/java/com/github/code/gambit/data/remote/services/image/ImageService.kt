package com.github.code.gambit.data.remote.services.image

import android.net.Uri
import com.github.code.gambit.helper.ServiceResult

interface ImageService {
    suspend fun uploadImage(imageUri: Uri): ServiceResult<String>
}
