package com.github.code.gambit.ui.fragment.home

import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.code.gambit.R
import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.databinding.FileListItemBinding
import com.github.code.gambit.ui.BaseAdapter
import com.github.code.gambit.ui.OnItemClickListener
import com.github.code.gambit.utility.extention.getIcon
import com.github.code.gambit.utility.extention.hide
import com.github.code.gambit.utility.extention.show

class FileListAdapter(val context: Context) :
    BaseAdapter<File, FileListItemBinding, FileUrlClickCallback>(R.layout.file_list_item) {

    private val viewPool = RecyclerView.RecycledViewPool()
    private val map = HashMap<String, UrlListAdapter>()

    override fun getBinding(view: View): FileListItemBinding {
        return FileListItemBinding.bind(view)
    }

    override fun bindViewHolder(position: Int, file: File, binding: FileListItemBinding) {
        binding.fileName.text = file.name
        binding.fileDate.text = file.timestamp
        binding.fileSize.text = file.size
        binding.fileIcon.setImageDrawable(ContextCompat.getDrawable(context, file.getIcon()))
        map[file.id] = UrlListAdapter(file.urls).apply {
            listener = object : OnItemClickListener<Url> {
                override fun onItemClick(item: Url) {
                    this@FileListAdapter.listener?.onUrlClick(item, file)
                }

                override fun onItemLongClick(item: Url) {
                    this@FileListAdapter.listener?.onUrlLongClick(item, file)
                }
            }
        }
        binding.urlList.apply {
            layoutManager =
                LinearLayoutManager(binding.urlList.context, RecyclerView.VERTICAL, false)
            setHasFixedSize(false)
            adapter = map[file.id]
            setRecycledViewPool(viewPool)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!binding.urlList.canScrollVertically(1)) {
                        listener?.onLoadMoreUrl(file)
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
            addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    when (e.action) {
                        MotionEvent.ACTION_MOVE -> {
                            rv.parent.requestDisallowInterceptTouchEvent(true)
                        }
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })
        }
        binding.closeButton.setOnClickListener {
            binding.urlListContainer.hide()
        }
        binding.createNewUrlButton.setOnClickListener { listener?.onCreateNewUrl(file) }
        binding.root.setOnLongClickListener {
            listener?.onItemLongClick(file)
            true
        }
    }

    override fun onItemClick(
        position: Int,
        item: File,
        binding: FileListItemBinding,
        listener: FileUrlClickCallback?
    ) {
        binding.urlListContainer.show()
        listener?.onItemClick(item)
    }

    fun addUrl(url: Url) {
        val file = getFile(url)
        file?.urls?.add(url)
        notifyUrlListUpdated(file?.id)
    }

    fun addUrls(urls: List<Url>, clearPrevious: Boolean = true) {
        val file = getFile(urls[0])
        file?.urls?.apply {
            if (clearPrevious) {
                this.clear()
            }
            this.addAll(urls)
        }
        notifyUrlListUpdated(urls[0].fileId)
    }

    private fun getFile(url: Url) = getDataList.find { it.id == url.fileId }

    private fun getUrlAdapter(fileId: String?): UrlListAdapter? = map[fileId]

    private fun notifyUrlListUpdated(fileId: String?) = getUrlAdapter(fileId)?.notifyDataSetChanged()
}
