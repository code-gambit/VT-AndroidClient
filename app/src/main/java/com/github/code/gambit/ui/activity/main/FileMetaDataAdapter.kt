package com.github.code.gambit.ui.activity.main

import android.content.Context
import android.view.View
import androidx.work.WorkInfo
import com.github.code.gambit.R
import com.github.code.gambit.data.model.FileUploadStatus
import com.github.code.gambit.databinding.FileMetaDataListItemBinding
import com.github.code.gambit.ui.BaseAdapter
import com.github.code.gambit.ui.OnItemClickListener

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
        binding.fileSize.text = item.fileMetaData.size.toString()
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
        val item = getDataList.find { it.fileMetaData.uuid == uuid }
        item?.state = state
        notifyDataSetChanged()
    }

    fun remove(item: FileUploadStatus) {
        getDataList.remove(item)
        notifyDataSetChanged()
    }
}
