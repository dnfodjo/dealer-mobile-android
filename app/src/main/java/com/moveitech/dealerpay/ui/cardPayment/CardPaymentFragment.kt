package com.moveitech.dealerpay.ui.cardPayment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moveitech.dealerpay.databinding.FragmentCardPaymentBinding
import com.moveitech.dealerpay.ui.BaseFragment

class CardPaymentFragment : BaseFragment<FragmentCardPaymentBinding>() {
    override fun initViews() {
    }

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCardPaymentBinding.inflate(layoutInflater, container, false)



    override fun liveDataObserver() {
    }

    override fun btnListener() {
        binding.btnNext.setOnClickListener {
            moveToNextScreen(CardPaymentFragmentDirections.actionCardPaymentFragmentToPaymentFragmentTwo())
        }
    }

}