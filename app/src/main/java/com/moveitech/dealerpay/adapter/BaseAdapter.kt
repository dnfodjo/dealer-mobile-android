package com.moveitech.dealerpay.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.moveitech.dealerpay.util.preventTwoClick

abstract class BaseAdapter<BINDING : ViewBinding>(
    var dataList: List<Any> = listOf(), var onItemClicker: OnItemClicker? = null
) : RecyclerView.Adapter<BaseAdapter.BaseViewHolder<BINDING>>() {

    abstract fun bind(binding: BINDING, item: Any, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BINDING> {
        val binder = getBinding(LayoutInflater.from(parent.context),parent)

        return BaseViewHolder(binder)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BINDING>, position: Int) {
        val model = dataList[position]
        bind(holder.binder, model,position)
        holder.itemView.setOnClickListener {
            onItemClicker?.onItemClick(position, model)
            holder.itemView.preventTwoClick()
        }
    }

    class BaseViewHolder<BINDING : ViewBinding>(val binder: BINDING) :
        RecyclerView.ViewHolder(binder.root) {
    }

    override fun getItemCount() = dataList.size

    interface OnItemClicker {
        fun onItemClick(position: Int, data: Any)
    }

    abstract fun setList(list:List<Any>)
    abstract fun getBinding(layoutInflater: LayoutInflater,container: ViewGroup?): BINDING

}

