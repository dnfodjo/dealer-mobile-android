package com.moveitech.dealerpay.ui

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.moveitech.dealerpay.adapter.TransactionAdapter
import com.moveitech.dealerpay.databinding.FragmentHomeBinding
import com.moveitech.dealerpay.util.closeKeyBoard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override fun initViews() {

        binding.searchView.onActionViewExpanded();
        binding.searchView.isIconified = true;
        setDefaultUi(showProfilePic = true)
        setupRecyclerView()
        setDefaultUi(true, showNavigationDrawer = true, showProfilePic = true)

        Handler(Looper.getMainLooper()).postDelayed({
            closeKeyBoard()

        },400)
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