package com.github.code.gambit.ui

interface OnItemClickListener<T> {
    fun onItemClick(item: T)
    fun onItemLongClick(item: T)
}
