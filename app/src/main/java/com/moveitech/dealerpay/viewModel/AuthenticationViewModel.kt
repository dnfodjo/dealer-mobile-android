package com.moveitech.dealerpay.viewModel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.moveitech.dealerpay.repository.ApiDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(private val dataRepository: ApiDataRepository) :
    BaseViewModel() {


    val email: ObservableField<String> = ObservableField("")
    val passWord: ObservableField<String> = ObservableField("")
    val loginResponse: MutableLiveData<Boolean> = MutableLiveData()
    val userNameError: MutableLiveData<Boolean> = MutableLiveData()
    val passwordError: MutableLiveData<Boolean> = MutableLiveData()
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"



    fun onClick(key: Int) {

        if (validateFields()) {
            loginResponse.value=true
            userNameError.value = false
            passwordError.value = false
        } else {
            userNameError.value = true
            passwordError.value = true
        }
    }

    private fun validateFields(): Boolean {
        var flag = true
        if (email.get()?.length ?: 0 == 0) {
            flag = false
        }

        if (passWord.get()?.length ?: 0 == 0) {
            flag = false
        }
        if (!email.get()?.matches(emailPattern.toRegex())!!)
        {
            flag=false
        }
        return flag
    }

//    private fun login() {
//        viewModelScope.launch {
//            showProgressBar(true)
//            dataRepository.login(userName.get().toString(), passWord.get().toString())
//                .let { response ->
//                    showProgressBar(false)
//
//                    when (response) {
//                        is ResultWrapper.Success -> {
//                            if (response.value.Code == 200) {
//                                loginResponse.value = response.value.Data
//                            } else {
//                                userNameError.value = true
//                                userNameError.value = true
//                            }
//                        }
//                        else -> handleErrorType(response)
//                    }
//                }
//        }
//    }
}