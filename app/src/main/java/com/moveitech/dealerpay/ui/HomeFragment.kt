package com.moveitech.dealerpay.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.moveitech.dealerpay.adapter.TransactionAdapter
import com.moveitech.dealerpay.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override fun initViews() {

        binding.searchView.onActionViewExpanded();
        binding.searchView.setIconified(true);
        setDefaultUi(showProfilePic = true)
        setupRecyclerView()
        setupRecyclerView()
        setDefaultUi(true, showNavigationDrawer = true, showProfilePic = true)

    }

    private fun setupRecyclerView() {

        val list = ArrayList<String>()
        binding.rvTransaction.apply {
            layoutManager= LinearLayoutManager(requireContext())
            for (i in 1..10)
            {
                list.add(i.toString())
            }
            adapter=TransactionAdapter(list)
        }
    }

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    )= FragmentHomeBinding.inflate(layoutInflater,container,false)

    override fun liveDataObserver() {
    }

    override fun btnListener() {
    }
}