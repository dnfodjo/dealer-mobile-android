package com.moveitech.dealerpay.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moveitech.dealerpay.databinding.HomeCardsBinding

class TransactionAdapter( list:ArrayList<String>):BaseAdapter<HomeCardsBinding>(list) {

    override fun bind(binding: HomeCardsBinding, item: Any, position: Int) {
    }

    override fun setList(list: List<Any>) {

    }

    override fun getBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    )= HomeCardsBinding.inflate(layoutInflater,container,false)
}