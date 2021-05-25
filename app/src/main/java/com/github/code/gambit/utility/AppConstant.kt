package com.github.code.gambit.utility

object AppConstant {
    const val AUTH_ATTRIBUTE_CUSTOM_PROFILE = "custom:profile_image"
    const val BASE_URL = "https://mhv71te0rh.execute-api.ap-south-1.amazonaws.com/beta/"
    const val SHARE_URL = "https://vt-webclient.herokuapp.com/"
    object RequestCode {
        const val PERMISSIONS = 101
        const val GALLERY = 100
        const val CAMERA = 102
        const val DOCUMENT = 103
    }

    object Named {
        const val PERMISSION_ARRAY = "PERMISSION"
        const val USER_ID = "UID"
        const val BASE_URL = "BRL"
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "ImageService related constants no longer used")
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

    object API_QUERY {
        const val FILE_LEK = "LastEvaluatedKey"
    }

    object Worker {
        const val FILE_URI_KEY = "FILE_URI"
        const val FILE_NAME_KEY = "FILE_NAME"
        const val FILE_SIZE_KEY = "FILE_SIZE"
        const val FILE_META_DATA = "FILE_META_DATA"
        const val USER_ID = "USER_ID"
        const val FILE_OUTPUT_KEY = "FILE_OUTPUT"
    }

    object Notification {
        const val CHANNEL_ID = "com.github.code.gambit.utility.notification.channel.id"
        const val CHANNEL_NAME = "V Transfer Notification"
        const val CHANNEL_DESCRIPTION = "Default notification channel for all V Transfer notifications"
    }

    object Database {
        const val DB_NAME = "V-TRANSFER-MAIN-DATABASE"
    }
}
