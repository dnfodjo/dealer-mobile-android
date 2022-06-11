package com.moveitech.dealerpay.ui.authentication

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moveitech.dealerpay.databinding.FragmentLoginBinding
import com.moveitech.dealerpay.ui.BaseFragment
import com.moveitech.dealerpay.util.DataStoreHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    @Inject
    lateinit var dataStoreHelper: DataStoreHelper

    override fun initViews() {
    }



    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(layoutInflater, container, false)

    override fun setDefaultUi() {
    }

    override fun liveDataObserver() {

    }



    override fun btnListener() {

    }
}