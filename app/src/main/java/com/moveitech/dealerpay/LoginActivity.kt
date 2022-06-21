package com.moveitech.dealerpay

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.moveitech.dealerpay.databinding.ActivityLoginBinding
import com.moveitech.dealerpay.util.DataStoreHelper
import com.moveitech.dealerpay.util.DialogUtils
import com.moveitech.dealerpay.util.showSnackBar
import com.moveitech.dealerpay.viewModel.AuthenticationViewModel
import com.moveitech.dealerpay.viewModel.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class LoginActivity : AppCompatActivity() {

    lateinit var binding:ActivityLoginBinding
    lateinit var dialog: AlertDialog

    @Inject
    lateinit var dataStoreHelper: DataStoreHelper
    private val viewModel: AuthenticationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel=viewModel
        dialog = DialogUtils.getProgressDialog(this)

        liveDataObserver()
    }

     fun liveDataObserver() {
        with(viewModel)
        {
            setupGeneralViewModel(this)

            userNameError.observe(this@LoginActivity)
            {
                setErrorOnFields(it)
            }

            loginResponse.observe(this@LoginActivity){
                if (it)
                {
                    loginResponse.value=false
                    val intent= Intent(this@LoginActivity,MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }
            }

        }

    }

    private fun setErrorOnFields(flag:Boolean) {

        if (flag) {
            binding.emailTextLayout.error = "Enter Valid Username"
            binding.passwordTextLayout.error = "Enter Valid Password"
        } else {
            binding.emailTextLayout.error = null
            binding.passwordTextLayout.error = null
        }
    }
    private fun setupGeneralViewModel(generalViewModel: BaseViewModel) {
        with(generalViewModel) {
            dialogMessage.observe(this@LoginActivity) {
//               showAlertDialog(it)
                showSnackBar(it)
            }

            progressBar.observe(this@LoginActivity) {
                showProgressDialog(it)

            }
        }
    }

    private fun showProgressDialog(show: Boolean) {

        if (show) {
            if (!dialog.isShowing)
                dialog.apply { show() }
        } else if (!show) {
            if (dialog.isShowing)
                dialog.dismiss()
        }
    }

}