package com.github.code.gambit.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.github.code.gambit.utility.extention.hide
import com.github.code.gambit.utility.extention.show

/**
 * Use as a base class for all the adapters in this project
 * @author Danish Jamal [https://github.com/danishjamal104]
 * @param [T] It describes the model class
 * @param [B] It describes the view binding class
 * @param [LS] It describes the listener class
 * @param layoutId layout reference which is to be inflated
 */
abstract class BaseAdapter<T, B : ViewBinding, LS : OnItemClickListener<T>>(private var layoutId: Int) :
    RecyclerView.Adapter<BaseAdapter.BaseViewHolder<B>>() {

    private val backupData = ArrayList<T>()
    private var dataList = ArrayList<T>()
    var listener: LS? = null
    private var counterView: TextView? = null
    private var emptyIndicatorView: View? = null

    /**
     * Adds the clickListener to the root view of [BaseViewHolder.binding], which calls the abstract
     * method [onItemClick]
     * Rest of the data binding must be implemented using  abstract method [bindViewHolder]
     */
    override fun onBindViewHolder(holder: BaseViewHolder<B>, position: Int) {
        val item = getItem(position)
        val binding = holder.binding
        binding.root.setOnClickListener {
            onItemClick(position, item, binding, listener)
        }
        bindViewHolder(position, item, binding)
    }

    /**
     * Return the viewBinding instance of type [B], this function is used by
     * @param view view instance of layout resource
     */
    abstract fun getBinding(view: View): B

    /**
     * Modify the view binding using the item. Define all the custom click events and view
     * manipulation here
     *
     * @param position <Int> : position of the item in the list view
     * @param item <T> : instance of the model
     * @param binding <B> : instance of the view binding
     */
    abstract fun bindViewHolder(position: Int, item: T, binding: B)

    /**
     * Define the events when root view of [binding] is clicked, to see where this function is used
     * refer [onBindViewHolder]
     */
    abstract fun onItemClick(position: Int, item: T, binding: B, listener: LS?)

    /**
     * Inflates the [layoutId] using [getBinding]
     * @return [BaseViewHolder]
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<B> {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return BaseViewHolder(getBinding(view))
    }

    /**
     * Adds the model object in [dataList] and notifies the adapter
     * @param data : instance of model [T]
     */
    fun add(data: T) {
        dataList.add(data)
        notifyDataSetChanged()
        updateCounterText()
        refreshEmptyIndicatorState()
    }

    /**
     * Adds the list of model object in [dataList] and notifies the adapter
     * @param dataItems : list of instance of model [T]
     * @param clearPrevious : set to `true` for clearing the previous items in dataList
     */
    fun addAll(dataItems: List<T>, clearPrevious: Boolean = false) {
        if (clearPrevious) {
            dataList.clear()
        }
        dataList.addAll(dataItems)
        notifyDataSetChanged()
        updateCounterText()
        refreshEmptyIndicatorState()
    }

    fun remove(item: T) {
        dataList.remove(item)
        updateCounterText()
        refreshEmptyIndicatorState()
        notifyDataSetChanged()
    }

    /**
     * Sets the counter text view which is used for displaying the live item count
     */
    fun bindCounterView(view: TextView) {
        counterView = view
    }

    /**
     * Sets the view which is displayed when list is empty
     */
    fun bindEmptyListView(view: View) {
        emptyIndicatorView = view
    }

    /**
     * updates the [counterView] with latest item count from [getItemCount]
     */
    @SuppressLint("SetTextI18n")
    private fun updateCounterText() {
        counterView?.text = "$itemCount Results"
    }

    /**
     * updates the [emptyIndicatorView] visibility based on latest item count from [getItemCount]
     */
    private fun refreshEmptyIndicatorState() {
        emptyIndicatorView?.let {
            if (itemCount == 0) {
                it.show()
            } else {
                it.hide()
            }
        }
    }

    /**
     * Returns the item <T> from [dataList] at specific position
     * @param position : position of item in [dataList]
     *
     * @return : element at [position] in [dataList]
     */
    private fun getItem(position: Int) = dataList[position]

    /**
     * @return : current size of [dataList]
     */
    override fun getItemCount() = dataList.size

    /**
     * get the [dataList] object
     */
    val getDataList get() = dataList

    /**
     * This method is used to set the [dataList] externally, mind that it doesn't notify the adapter
     * about changes hence it should only be used before view in inflated.
     *
     * @param dataItems : list of items
     */
    fun setDataList(dataItems: ArrayList<T>) {
        dataList = dataItems
    }

    /**
     * stores the current item for later user and clears the [dataList]
     */
    fun backup() {
        backupData.addAll(dataList)
        dataList.clear()
        updateCounterText()
        refreshEmptyIndicatorState()
        notifyDataSetChanged()
    }

    /**
     * restores the [dataList] to the latest [backupData]
     */
    fun restore() {
        dataList.clear()
        dataList.addAll(backupData)
        backupData.clear()
        updateCounterText()
        refreshEmptyIndicatorState()
        notifyDataSetChanged()
    }

    /**
     * ViewHolder class for containing views for [BaseAdapter], This class is same for all the
     * adapters in this application. Parent class is not required to define custom ViewHolder, all
     * modifications and binding are exposed through adapter class methods.
     *
     * @author Danish Jamal <https://github.com/danishjamal104>
     * @param [B] : describe the type of view binding class
     * @param [_binding] : view binding instance of type [ViewBinding]
     */
    @Suppress("UNCHECKED_CAST")
    class BaseViewHolder<B : ViewBinding>(private val _binding: ViewBinding) :
        RecyclerView.ViewHolder(_binding.root) {

        /**
         * returns the view view binding instance of type [B]
         */
        val binding get() = _binding as B
    }
}
