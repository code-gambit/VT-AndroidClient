package com.github.code.gambit.ui.activity.main

import android.content.Context
import android.view.View
import androidx.work.WorkInfo
import com.github.code.gambit.R
import com.github.code.gambit.data.model.FileUploadStatus
import com.github.code.gambit.databinding.FileMetaDataListItemBinding
import com.github.code.gambit.ui.BaseAdapter
import com.github.code.gambit.ui.OnItemClickListener
import com.github.code.gambit.utility.extention.byteToMb
import timber.log.Timber

class FileMetaDataAdapter(val context: Context) :
    BaseAdapter<FileUploadStatus, FileMetaDataListItemBinding, OnItemClickListener<FileUploadStatus>>(
        R.layout.file_meta_data_list_item
    ) {

    override fun getBinding(view: View): FileMetaDataListItemBinding {
        return FileMetaDataListItemBinding.bind(view)
    }

    override fun bindViewHolder(
        position: Int,
        item: FileUploadStatus,
        binding: FileMetaDataListItemBinding
    ) {
        binding.fileName.text = item.fileMetaData.name
        binding.fileSize.text = item.fileMetaData.size.byteToMb()
        binding.filePath.text = item.state.toString()
    }

    override fun onItemClick(
        position: Int,
        item: FileUploadStatus,
        binding: FileMetaDataListItemBinding,
        listener: OnItemClickListener<FileUploadStatus>?
    ) {
        listener?.onItemClick(item)
    }

    fun updateStatus(uuid: String, state: WorkInfo.State) {
        val dt = getDataList
        dt.find { it.fileMetaData.uuid == uuid }?.let {
            it.state = state
            Timber.tag("work").i("${it.fileMetaData.uuid} updated to $state")
            notifyDataSetChanged()
        }
    }
}
