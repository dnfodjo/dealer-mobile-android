package com.moveitech.dealerpay.repository


import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ApiDataRepository @Inject constructor(){

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

//    suspend fun login(userName:String,password:String): ResultWrapper<LoginResponse> {
//        return safeApiCall(dispatcher) { RetrofitClient.getApi().loginRequest(userName,password) }
//    }



}