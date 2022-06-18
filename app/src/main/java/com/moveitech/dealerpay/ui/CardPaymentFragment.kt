package com.moveitech.dealerpay.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moveitech.dealerpay.databinding.FragmentCardPaymentBinding
import com.moveitech.dealerpay.databinding.FragmentLoginBinding
import com.moveitech.dealerpay.ui.BaseFragment

class CardPaymentFragment : BaseFragment<FragmentCardPaymentBinding>() {
    override fun initViews() {
    }

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCardPaymentBinding.inflate(layoutInflater, container, false)

    override fun setDefaultUi() {
    }

    override fun liveDataObserver() {
    }

    override fun btnListener() {
    }

}