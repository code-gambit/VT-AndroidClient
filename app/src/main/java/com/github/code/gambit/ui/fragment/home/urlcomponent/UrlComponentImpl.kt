package com.github.code.gambit.ui.fragment.home.urlcomponent

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.github.code.gambit.R
import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.databinding.UrlEditLayoutBinding
import com.github.code.gambit.utility.extention.hide
import com.github.code.gambit.utility.extention.show

class UrlComponentImpl(val context: Context) : UrlComponent {

    private val dialog = Dialog(context)

    @SuppressLint("InflateParams")
    private val _binding = UrlEditLayoutBinding.bind(
        LayoutInflater.from(context).inflate(R.layout.url_edit_layout, null)
    )
    private val binding get() = _binding

    private lateinit var onCancel: () -> Unit
    private lateinit var onUpdate: (url: Url) -> Unit
    private lateinit var onDelete: (url: Url) -> Unit

    private var url: Url? = null
    private var file: File? = null

    init {
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.cancel.setOnClickListener {
            dismiss()
            onCancel()
        }
        binding.submit.setOnClickListener {
            val visibility = binding.visibility.isChecked
            val clickCount = binding.clickCount.value.toInt()

            if (url?.clicksLeft == clickCount && url?.visible == visibility) {
                dismiss(true, R.string.no_change)
            } else {
                url?.let {
                    it.visible = visibility
                    it.clicksLeft = clickCount
                    toggleProgressState()
                    onUpdate(it)
                }
            }
        }
        binding.delete.setOnClickListener {
            url?.let {
                toggleProgressState()
                onDelete(it)
            }
        }
    }

    fun addOnCancelCallback(clb: () -> Unit) {
        onCancel = clb
    }

    fun addOnUpdateCallback(clb: (url: Url) -> Unit) {
        onUpdate = clb
    }

    fun addOnDeleteCallback(clb: (url: Url) -> Unit) {
        onDelete = clb
    }

    private fun toggleProgressState(force: Boolean = false, showProgress: Boolean = true) {
        val hideCondition = if (force) {
            !showProgress
        } else {
            binding.progressBar.isVisible
        }
        if (hideCondition) {
            (binding.progressBar as View).hide()
            binding.submit.apply {
                this.show()
                this.isEnabled = true
            }
            binding.cancel.isEnabled = true
            binding.delete.isEnabled = true
        } else {
            (binding.progressBar as View).show()
            binding.submit.apply {
                this.hide()
                this.isEnabled = false
            }
            binding.cancel.isEnabled = false
            binding.delete.isEnabled = false
        }
    }

    private fun dismiss(showToast: Boolean = false, @StringRes toastMessage: Int = R.string.ok) {
        if (showToast) {
            Toast.makeText(context, context.getString(toastMessage), Toast.LENGTH_SHORT).show()
        }
        dialog.dismiss()
    }

    private fun reset() {
        toggleProgressState(force = true, showProgress = false)
        url?.let {
            binding.clickCount.value = it.clicksLeft.toFloat()
            binding.visibility.isChecked = it.visible
            binding.urlId.text = "/${it.id}"
        }
    }

    override fun show(url: Url, file: File) {
        this.url = url
        this.file = file
        reset()
        dialog.show()
    }

    override fun hide() {
        toggleProgressState(force = true, showProgress = false)
        dismiss()
    }
}
