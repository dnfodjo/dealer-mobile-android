package com.moveitech.dealerpay.repository

import com.moveitech.dealerpay.network.ResultWrapper
import com.moveitech.dealerpay.dataModel.utilModel.ApiErrorResponse
import com.moveitech.dealerpay.util.ErrorUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is ConnectException -> ResultWrapper.ConnectNetworkError
                is IOException -> ResultWrapper.NetworkError
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    ResultWrapper.GenericError(code, errorResponse)
                }
                else -> {
                    ResultWrapper.GenericError(0, ApiErrorResponse(0, "" + throwable.message))
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): ApiErrorResponse? {
    return try {
        throwable.response()?.errorBody()?.let {
            return ErrorUtils.parseError(it.string(), throwable)
        }
    } catch (exception: Exception) {
        ApiErrorResponse(throwable.code(), throwable.message())
    }
}