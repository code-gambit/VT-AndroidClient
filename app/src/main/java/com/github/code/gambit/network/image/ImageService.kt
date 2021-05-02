package com.github.code.gambit.network.image

import android.net.Uri
import com.github.code.gambit.helper.auth.ServiceResult

interface ImageService {
    suspend fun uploadImage(imageUri: Uri): ServiceResult<String>
}
