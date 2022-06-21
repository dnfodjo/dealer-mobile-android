package com.moveitech.dealerpay.ui.requestPayment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moveitech.dealerpay.databinding.FragmentPaymenRequestTwoBinding
import com.moveitech.dealerpay.ui.BaseFragment

class PaymentReqTwoFragment : BaseFragment<FragmentPaymenRequestTwoBinding>() {
    override fun initViews() {
        setDefaultUi(showNavigationDrawer = false, showProfilePic = false, showToolbar = true)
    }

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPaymenRequestTwoBinding.inflate(layoutInflater, container, false)



    override fun liveDataObserver() {
    }

    override fun btnListener() {
    }

}