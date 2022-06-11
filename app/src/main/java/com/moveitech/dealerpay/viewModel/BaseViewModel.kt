package com.moveitech.dealerpay.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.moveitech.dealerpay.network.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() :ViewModel(){

     val dialogMessage = MutableLiveData<String>()
    val progressBar = MutableLiveData<Boolean>()

    protected fun showDialogMessage(message: String) {
        dialogMessage.value = message
    }



    protected fun showProgressBar(show: Boolean) {
        progressBar.value = show
    }

    protected fun handleErrorType(error: ResultWrapper<Any>) {
        showProgressBar(false)
        when (error) {
            is ResultWrapper.NetworkError ->
                showDialogMessage("Internet not available")

            is ResultWrapper.GenericError ->
                showDialogMessage("" + error.error?.message)
        }
    }
}