package com.moveitech.dealerpay.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moveitech.dealerpay.R
import com.moveitech.dealerpay.databinding.FragmentLoginBinding
import com.moveitech.dealerpay.databinding.FragmentPaymenRequestTwoBinding

class PaymentReqTwoFragment : BaseFragment<FragmentPaymenRequestTwoBinding>() {
    override fun initViews() {
        setDefaultUi(showNavigationDrawer = false, showProfilePic = false)
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