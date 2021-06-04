package com.github.code.gambit.utility.extention

import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.github.code.gambit.R
import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.User
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.utility.AppConstant
import java.util.Base64
import kotlin.math.pow

fun String.toBase64(): String {
    val byteArray = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        Base64.getEncoder().encode(this.toByteArray())
    } else {
        android.util.Base64.encode(this.toByteArray(), android.util.Base64.DEFAULT)
    }
    return String(byteArray)
}

fun String.fromBase64(): String {
    val byteArray = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        Base64.getDecoder().decode(this)
    } else {
        android.util.Base64.decode(this, android.util.Base64.DEFAULT)
    }
    return String(byteArray)
}

fun AuthSignUpOptions.Builder<*>.defaultBuilder(authData: AuthData): AuthSignUpOptions {
    return userAttributes(
        mutableListOf
        (
            AuthUserAttribute(AuthUserAttributeKey.email(), authData.email),
            AuthUserAttribute(AuthUserAttributeKey.custom(AppConstant.AUTH_ATTRIBUTE_CUSTOM_PROFILE), authData.thumbnail),
            AuthUserAttribute(AuthUserAttributeKey.name(), authData.fullname)
        )
    ).build()
}

fun AuthSignUpOptions.Builder<*>.defaultBuilder(user: User): AuthSignUpOptions {
    return userAttributes(
        mutableListOf
        (
            AuthUserAttribute(AuthUserAttributeKey.email(), user.email),
            AuthUserAttribute(AuthUserAttributeKey.name(), user.name)
        )
    ).build()
}

fun Int.byteToMb(): String {
    return String.format("%.2f", this.div(10.0.pow(6.0)))
}

fun String.toSharableUrl(): String = "${AppConstant.SHARE_URL}$this"

fun File.getIcon(): Int {
    return when (this.type.lowercase()) {
        AppConstant.FileType.PDF -> R.drawable.ic_pdf
        AppConstant.FileType.EPUB -> R.drawable.ic_epub
        AppConstant.FileType.TXT -> R.drawable.ic_txt
        AppConstant.FileType.RTF -> R.drawable.ic_rtf
        in AppConstant.FileType.ANDROID -> R.drawable.ic_apk
        in AppConstant.FileType.DOC -> R.drawable.ic_doc
        in AppConstant.FileType.EXCEL -> R.drawable.ic_xls
        in AppConstant.FileType.PPT -> R.drawable.ic_ppt
        in AppConstant.FileType.IMAGE -> R.drawable.ic_image
        in AppConstant.FileType.AUDIO -> R.drawable.ic_audio
        in AppConstant.FileType.VIDEO -> R.drawable.ic_video
        in AppConstant.FileType.SCRIPT -> R.drawable.ic_script
        in AppConstant.FileType.COMPRESS -> R.drawable.ic_compress
        else -> R.drawable.ic_unknown
    }
}
