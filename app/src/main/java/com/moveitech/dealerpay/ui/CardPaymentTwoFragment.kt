package com.moveitech.dealerpay.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moveitech.dealerpay.databinding.FragmentLoginBinding
import com.moveitech.dealerpay.databinding.FragmentPaymentTwoBinding

class CardPaymentTwoFragment:BaseFragment<FragmentPaymentTwoBinding> (){
    override fun initViews() {
    }

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPaymentTwoBinding.inflate(layoutInflater, container, false)


    override fun setDefaultUi() {
    }

    override fun liveDataObserver() {
    }

    override fun btnListener() {
    }
}