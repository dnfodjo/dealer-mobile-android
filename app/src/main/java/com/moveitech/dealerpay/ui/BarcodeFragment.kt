package com.moveitech.dealerpay.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moveitech.dealerpay.R
import com.moveitech.dealerpay.databinding.FragmentBarcodeBinding
import com.moveitech.dealerpay.databinding.FragmentCardPaymentBinding

class BarcodeFragment : BaseFragment<FragmentBarcodeBinding>() {
    override fun initViews() {
    }

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    )=FragmentBarcodeBinding.inflate(layoutInflater, container, false)



    override fun liveDataObserver() {
    }

    override fun btnListener() {
    }

}