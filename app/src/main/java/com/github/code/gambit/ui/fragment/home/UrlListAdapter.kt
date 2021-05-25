package com.github.code.gambit.ui.fragment.home

import android.view.View
import com.github.code.gambit.R
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.databinding.UrlListItemBinding
import com.github.code.gambit.ui.BaseAdapter
import com.github.code.gambit.ui.OnItemClickListener

class UrlListAdapter(urlList: ArrayList<Url>) :
    BaseAdapter<Url, UrlListItemBinding, OnItemClickListener<Url>>(R.layout.url_list_item) {

    init {
        setDataList(urlList)
    }

    override fun getBinding(view: View): UrlListItemBinding = UrlListItemBinding.bind(view)

    override fun bindViewHolder(position: Int, item: Url, binding: UrlListItemBinding) {
        binding.id.text = item.id
        binding.root.setOnLongClickListener {
            listener?.onItemLongClick(item)
            true
        }
    }

    override fun onItemClick(
        position: Int,
        item: Url,
        binding: UrlListItemBinding,
        listener: OnItemClickListener<Url>?
    ) {
        listener?.onItemClick(item)
    }
}
