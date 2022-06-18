package com.moveitech.dealerpay.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moveitech.dealerpay.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    override fun initViews() {

        setDefaultUi(false, showNavigationDrawer = false)
    }

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSettingsBinding.inflate(layoutInflater, container, false)



    override fun liveDataObserver() {
    }

    override fun btnListener() {
    }

}