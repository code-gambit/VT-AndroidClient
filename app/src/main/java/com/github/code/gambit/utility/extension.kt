package com.github.code.gambit.utility

import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import java.util.Base64

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

fun Window.setStatusColor(color: Int) {
    this.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    this.statusBarColor = color
}

fun Window.fullscreen() {
    this.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun Window.exitFullscreen() {
    this.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun View.anim(delay: Long) {
    this.animate().translationY(0f)
        .alpha(1f)
        .setInterpolator(DecelerateInterpolator())
        .setStartDelay(delay).setDuration(700).start()
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun List<View>.hideAll() {
    this.forEach { it.hide() }
}

fun List<View>.showAll() {
    this.forEach { it.show() }
}
