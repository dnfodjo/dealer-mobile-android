package com.moveitech.dealerpay.ui.requestPayment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moveitech.dealerpay.databinding.FragmentPaymentRequestOneBinding
import com.moveitech.dealerpay.ui.BaseFragment

class PaymentRequestOne : BaseFragment<FragmentPaymentRequestOneBinding>() {
    override fun initViews() {
        setDefaultUi(showProfilePic = true)
    }

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPaymentRequestOneBinding.inflate(layoutInflater, container, false)



    override fun liveDataObserver() {
    }

    override fun btnListener() {
        binding.btnNext?.setOnClickListener {
            moveToNextScreen(PaymentRequestOneDirections.actionPaymentRequestOneToPaymentReqTwoFragment())
        }
    }

}