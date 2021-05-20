package com.github.code.gambit.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.code.gambit.R
import com.github.code.gambit.data.model.File
import com.github.code.gambit.databinding.FileListItemBinding

class FileListAdapter(
    var context: Context,
    var fileList: ArrayList<File>
) : RecyclerView.Adapter<FileListAdapter.FileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_list_item, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = fileList[position]
        holder.binding.fileName.text = file.name
        holder.binding.fileDate.text = file.timestamp
        holder.binding.fileSize.text = file.size
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    fun add(file: File) {
        fileList.add(file)
        notifyDataSetChanged()
    }

    fun addAll(files: List<File>, clearPrevious: Boolean = false) {
        if (clearPrevious) {
            fileList.clear()
        }
        fileList.addAll(files)
        notifyDataSetChanged()
    }

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var _binding: FileListItemBinding = FileListItemBinding.bind(itemView)
        val binding get() = _binding
    }
}
