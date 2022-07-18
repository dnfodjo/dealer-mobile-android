package com.moveitech.dealerpay.ui.requestPayment

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moveitech.dealerpay.databinding.FragmentPaymentRequestTwoBinding
import com.moveitech.dealerpay.ui.BaseFragment
import com.moveitech.dealerpay.ui.PaymentInte.PaymentInteActivity

class PaymentReqTwoFragment : BaseFragment<FragmentPaymentRequestTwoBinding>() {
    override fun initViews() {
        setDefaultUi(showNavigationDrawer = false, showProfilePic = false, showToolbar = true)
    }

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPaymentRequestTwoBinding.inflate(layoutInflater, container, false)


    override fun liveDataObserver() {
    }

    override fun btnListener() {
        binding.btnSendRequest.setOnClickListener {
            val intent = Intent(requireActivity(), PaymentInteActivity::class.java)
            startActivity(intent)
        }
    }

}