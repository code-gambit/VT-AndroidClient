package com.github.code.gambit.utility.extention

import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import com.github.code.gambit.R
import com.google.android.material.snackbar.Snackbar

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
    if (visibility != View.GONE) {
        this.visibility = View.GONE
    }
}

fun View.show() {
    if (visibility != View.VISIBLE) {
        this.visibility = View.VISIBLE
    }
}

fun View.toggleVisibility() {
    if (isVisible) {
        visibility = View.GONE
        return
    }
    visibility = View.VISIBLE
}

fun List<View>.hideAll() {
    this.forEach { it.hide() }
}

fun List<View>.showAll() {
    this.forEach { it.show() }
}

fun View.snackbar(message: String, view: View) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAnchorView(view)
        .also { snackbar ->
            snackbar.setAction("OK") {
                snackbar.dismiss()
            }
                .show()
        }
}

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .also { snackbar ->
            snackbar.setAction("OK") {
                snackbar.dismiss()
            }
                .show()
        }
}

fun RelativeLayout.bottomNavHide() {
    if (isVisible) {
        visibility = View.GONE
        animation = AnimationUtils.loadAnimation(context, R.anim.bottom_nav_hide)
    }
}

fun RelativeLayout.bottomNavShow() {
    if (!isVisible) {
        visibility = View.VISIBLE
        animation = AnimationUtils.loadAnimation(context, R.anim.bottom_nav_show)
    }
}
