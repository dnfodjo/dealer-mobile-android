package com.moveitech.dealerpay.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moveitech.dealerpay.databinding.FragmentHomeBinding
import com.moveitech.dealerpay.databinding.FragmentLoginBinding
import com.moveitech.dealerpay.ui.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override fun initViews() {
    }

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(layoutInflater, container, false)


    override fun setDefaultUi() {
    }

    override fun liveDataObserver() {
    }

    override fun btnListener() {
    }

}