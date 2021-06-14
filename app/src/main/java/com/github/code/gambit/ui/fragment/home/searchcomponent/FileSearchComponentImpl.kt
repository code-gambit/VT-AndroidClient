package com.github.code.gambit.ui.fragment.home.searchcomponent

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.code.gambit.R
import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.databinding.SearchLayoutBinding
import com.github.code.gambit.ui.fragment.home.FileListAdapter
import com.github.code.gambit.ui.fragment.home.FileUrlClickCallback
import com.github.code.gambit.utility.extention.hide
import com.github.code.gambit.utility.extention.show

class FileSearchComponentImpl(
    private val binding: SearchLayoutBinding,
    private val adapter: FileListAdapter
) : FileSearchComponent, FileUrlClickCallback {

    val fileSearchRequest = MutableLiveData<String>()
    private var context: Context? = null

    fun registerComponents(ctx: Context, closeFunc: () -> Unit) {
        this.context = ctx
        adapter.bindCounterView(binding.counter)
        binding.fileList.layoutManager = LinearLayoutManager(context)
        binding.fileList.setHasFixedSize(false)
        binding.fileList.adapter = adapter
        adapter.listener = this
        binding.homeButton.setOnClickListener {
            setIllustrationState(true)
            closeFunc()
        }
        binding.searchInput.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString()
                if (text.length >= 3) {
                    newSearch(text)
                } else if (text.isEmpty()) {
                    setIllustrationState(true)
                    adapter.addAll(listOf(), true)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        show()
    }

    override fun show() {
        binding.searchInput.editText?.setText("")
        adapter.bindEmptyView(binding.illustration)
        adapter.refresh(true)
    }

    private fun newSearch(searchString: String) {
        fileSearchRequest.postValue(searchString)
    }

    override fun setRefreshing() {
        (binding.progressBar as View).show()
    }

    override fun setFileLoaded(files: List<File>) {
        (binding.progressBar as View).hide()
        adapter.addAll(files as ArrayList<File>, true)
        if (files.isEmpty()) {
            setIllustrationState()
        }
    }

    /**
     * @param isFirstState `true` if want to set the state to initial/first state else to second
     */
    private fun setIllustrationState(isFirstState: Boolean = false) {
        context?.let {
            if (isFirstState) {
                binding.illustration.setImageDrawable(
                    ContextCompat.getDrawable(it, R.drawable.illus_void)
                )
                binding.infoMessage.text = it.getString(R.string.file_search_info)
            } else {
                binding.illustration.setImageDrawable(
                    ContextCompat.getDrawable(it, R.drawable.illus_error)
                )
                binding.infoMessage.text = it.getString(R.string.empty_file_search_result)
            }
        }
    }

    override fun getRequests(): MutableLiveData<String> {
        return fileSearchRequest
    }

    override fun onCreateNewUrl(file: File) = fileSearchRequest.postValue("")

    override fun onLoadMoreUrl(file: File) = fileSearchRequest.postValue("")

    override fun onUrlLongClick(url: Url, file: File) = fileSearchRequest.postValue("")

    override fun onUrlClick(url: Url, file: File) = fileSearchRequest.postValue("")

    override fun onItemClick(item: File) = fileSearchRequest.postValue("")

    override fun onItemLongClick(item: File) = fileSearchRequest.postValue("")
}
