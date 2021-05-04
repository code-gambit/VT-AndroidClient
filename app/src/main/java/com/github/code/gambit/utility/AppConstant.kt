package com.github.code.gambit.utility

object AppConstant {
    const val AUTH_ATTRIBUTE_CUSTOM_PROFILE = "custom:profile_image"
    object RequestCode {
        const val PERMISSIONS = 101
    }

    object Named {
        const val PERMISSION_ARRAY = "PERMISSION"
        const val USER_ID = "UID"
    }

    object Cloudinary {
        const val UPLOAD_PRESET = "mqlj7ft0"
        const val RESULT_URL_KEY = "url"
        const val BASE_URL =
            "http://res.cloudinary.com/code-gambit/image/upload/v1619956278/Profile%20Image/"
    }

    object API_PATH {
        const val USER_ID = "userId"
        const val FILE_ID = "fileId"
        const val URL_ID = "urlId"
    }
}
