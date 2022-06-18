package com.moveitech.dealerpay.viewModel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moveitech.dealerpay.R
import com.moveitech.dealerpay.databinding.FragmentLoginBinding
import com.moveitech.dealerpay.databinding.FragmentPaymentTwoBinding
import com.moveitech.dealerpay.ui.BaseFragment

class PaymentFragmentTwo : BaseFragment<FragmentPaymentTwoBinding>() {
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