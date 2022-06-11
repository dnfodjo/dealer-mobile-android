package com.moveitech.dealerpay.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.moveitech.dealerpay.util.DialogUtils
import com.moveitech.dealerpay.util.safeNavigate
import com.moveitech.dealerpay.util.showSnackBar
import com.moveitech.dealerpay.viewModel.BaseViewModel


abstract class BaseFragment <T: ViewBinding>:Fragment() {

    protected lateinit var binding:T
    lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog = DialogUtils.getProgressDialog(requireContext())

        binding =getFragmentBinding(layoutInflater,container)
       return binding.root
    }


    abstract fun initViews()


    abstract fun getFragmentBinding(layoutInflater: LayoutInflater,container: ViewGroup?): T




    abstract fun setDefaultUi()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setDefaultUi()
        liveDataObserver()
    }

    abstract fun  liveDataObserver()

    abstract fun btnListener()
    protected fun setupGeneralViewModel(generalViewModel: BaseViewModel) {
        with(generalViewModel) {
            dialogMessage.observe(viewLifecycleOwner) {
//               showAlertDialog(it)
                showSnackBar(it)
            }

            progressBar.observe(viewLifecycleOwner) {
                    showProgressDialog(it)

            }
        }
    }


    protected fun showProgressDialog(show: Boolean) {

        if (show) {
            if (!dialog.isShowing)
                dialog.apply { show() }
        } else if (!show) {
            if (dialog.isShowing)
                dialog.dismiss()
        }
    }

    protected fun moveToNextScreen(navDirections: NavDirections)
    {
         findNavController().safeNavigate(navDirections)
    }
}